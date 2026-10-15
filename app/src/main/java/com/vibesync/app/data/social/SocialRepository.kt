package com.vibesync.app.data.social

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class SocialActionResult {
    object Success : SocialActionResult()
    data class Error(val message: String) : SocialActionResult()
}

private class UsernameTakenException : Exception()

/**
 * Firestore-backed friends/usernames/party-invites, all client + security-rules only (no backend,
 * same architecture as [com.vibesync.app.data.party.PartyRepository]). See `FIRESTORE_RULES.md`
 * for the rules this data model assumes.
 *
 * - `usernames/{usernameLower}` is a reservation doc (`{uid}`) used purely to make usernames
 *   unique -- created/deleted inside the same transaction as `users/{uid}.username` so the two
 *   never disagree about who currently owns a name.
 * - `users/{uid}/friends/{friendUid}` -- each side of a friendship writes its own copy. Adding a
 *   friend is a single client-side batch (both docs at once, no approval step) made possible by a
 *   security-rule trick: you're always allowed to write *yourself* into someone else's friends
 *   subcollection (never an arbitrary other uid), so the reciprocal write doesn't need the other
 *   person's client to be the one performing it.
 * - `partyInvites/{id}` -- a real accept/reject flow (unlike adding a friend), since accepting one
 *   means actually joining someone's live party.
 */
class SocialRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersRef = firestore.collection("users")
    private val usernamesRef = firestore.collection("usernames")
    private val invitesRef = firestore.collection("partyInvites")

    private fun friendsRef(uid: String) = usersRef.document(uid).collection("friends")

    private fun friendlyFirestoreError(e: Exception, action: String): String {
        val permissionDenied = (e as? com.google.firebase.firestore.FirebaseFirestoreException)?.code ==
            com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED
        return if (permissionDenied) {
            "Couldn't $action -- this app's Firestore security rules need to be re-published in the " +
                "Firebase console (see FIRESTORE_RULES.md)."
        } else {
            e.message ?: "Couldn't $action."
        }
    }

    /** Claims [desired] as this user's unique, case-insensitively-reserved username, freeing
     *  whatever username they held before (if any). Fails with a plain "already taken" message
     *  (not a raw Firestore error) if someone else holds it -- checked and claimed atomically via
     *  a transaction, so two people racing for the same name can't both win. */
    suspend fun setUsername(uid: String, desired: String): SocialActionResult {
        val trimmed = desired.trim()
        if (trimmed.length < 3 || trimmed.length > 20 || !trimmed.matches(Regex("^[A-Za-z0-9_]+$"))) {
            return SocialActionResult.Error("Usernames are 3-20 characters: letters, numbers, and underscore only.")
        }
        val lower = trimmed.lowercase()
        return try {
            firestore.runTransaction { tx ->
                val reservationRef = usernamesRef.document(lower)
                val reservation = tx.get(reservationRef)
                if (reservation.exists() && reservation.getString("uid") != uid) {
                    throw UsernameTakenException()
                }
                val userRef = usersRef.document(uid)
                val oldLower = tx.get(userRef).getString("usernameLower")
                if (oldLower != null && oldLower != lower) {
                    tx.delete(usernamesRef.document(oldLower))
                }
                tx.set(reservationRef, mapOf("uid" to uid))
                tx.update(userRef, mapOf("username" to trimmed, "usernameLower" to lower))
            }.await()
            SocialActionResult.Success
        } catch (e: UsernameTakenException) {
            SocialActionResult.Error("\"$trimmed\" is already taken -- try another.")
        } catch (e: Exception) {
            SocialActionResult.Error(friendlyFirestoreError(e, "set your username"))
        }
    }

    fun observeMyProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val registration = usersRef.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) android.util.Log.e("SocialRepository", "observeMyProfile failed", error)
            trySend(snapshot?.let { UserProfile.fromSnapshot(it) })
        }
        awaitClose { registration.remove() }
    }

    /** Best-effort, not real presence -- see [ONLINE_WINDOW_MILLIS]'s doc comment. Called on a
     *  timer from [com.vibesync.app.ui.MainViewModel] while the app is in the foreground. */
    suspend fun sendHeartbeat(uid: String) {
        try {
            usersRef.document(uid).update("lastSeenAt", FieldValue.serverTimestamp()).await()
        } catch (_: Exception) { /* best-effort, next tick will retry */ }
    }

    /** Prefix search on the (lowercased) username -- a single-field range query, so no manual
     *  Firestore composite index is needed, same "keep setup to just the one rules paste"
     *  constraint the rest of this app's Firestore usage follows. Case-insensitive by construction
     *  since [usernameLower] is what's indexed against. */
    suspend fun searchUsersByUsername(prefix: String): List<UserProfile> {
        val lower = prefix.trim().lowercase()
        if (lower.isBlank()) return emptyList()
        return try {
            usersRef.orderBy("usernameLower").startAt(lower).endAt(lower + "").limit(20)
                .get().await().documents.mapNotNull { UserProfile.fromSnapshot(it) }
        } catch (e: Exception) {
            android.util.Log.e("SocialRepository", "searchUsersByUsername failed", e)
            emptyList()
        }
    }

    fun observeFriends(uid: String): Flow<List<Friend>> = callbackFlow {
        val registration = friendsRef(uid).addSnapshotListener { snapshot, error ->
            if (error != null) android.util.Log.e("SocialRepository", "observeFriends failed -- check Firestore rules are published", error)
            trySend(snapshot?.documents?.mapNotNull { Friend.fromSnapshot(it) } ?: emptyList())
        }
        awaitClose { registration.remove() }
    }

    /** Immediate, mutual, no approval step -- both `users/{me}/friends/{them}` and
     *  `users/{them}/friends/{me}` are written in one batch. See the class doc comment for the
     *  security-rule trick that makes the second (writing into *their* subcollection) legal. */
    suspend fun addFriend(myUid: String, myDisplayName: String, myUsername: String?, friend: UserProfile): SocialActionResult {
        return try {
            val now = FieldValue.serverTimestamp()
            val batch = firestore.batch()
            batch.set(friendsRef(myUid).document(friend.uid), mapOf("uid" to friend.uid, "displayName" to friend.displayName, "username" to friend.username, "addedAt" to now))
            batch.set(friendsRef(friend.uid).document(myUid), mapOf("uid" to myUid, "displayName" to myDisplayName, "username" to myUsername, "addedAt" to now))
            batch.commit().await()
            SocialActionResult.Success
        } catch (e: Exception) {
            SocialActionResult.Error(friendlyFirestoreError(e, "add that friend"))
        }
    }

    suspend fun removeFriend(myUid: String, friendUid: String): SocialActionResult {
        return try {
            val batch = firestore.batch()
            batch.delete(friendsRef(myUid).document(friendUid))
            batch.delete(friendsRef(friendUid).document(myUid))
            batch.commit().await()
            SocialActionResult.Success
        } catch (e: Exception) {
            SocialActionResult.Error(friendlyFirestoreError(e, "remove that friend"))
        }
    }

    /** Host-only (enforced by security rules, which check the party doc's own `hostUid`). Refuses
     *  a duplicate still-pending invite to the same friend for the same party -- three equality
     *  filters, still no composite index needed. */
    suspend fun sendPartyInvite(fromUid: String, fromDisplayName: String, toUid: String, partyCode: String, partyName: String): SocialActionResult {
        return try {
            val existing = invitesRef
                .whereEqualTo("toUid", toUid)
                .whereEqualTo("partyCode", partyCode)
                .whereEqualTo("status", "pending")
                .get().await()
            if (!existing.isEmpty) return SocialActionResult.Error("Already invited them to this party.")
            invitesRef.add(
                mapOf(
                    "fromUid" to fromUid,
                    "fromDisplayName" to fromDisplayName,
                    "toUid" to toUid,
                    "partyCode" to partyCode,
                    "partyName" to partyName,
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            SocialActionResult.Success
        } catch (e: Exception) {
            SocialActionResult.Error(friendlyFirestoreError(e, "send that invite"))
        }
    }

    /** In-app only, by design (see version25/CHANGES.txt) -- this only reflects new invites while
     *  the app is actually running (foreground or briefly backgrounded), not a true push
     *  notification delivered while the app is fully closed. Real push would need Firebase Cloud
     *  Messaging plus a server-side trigger (a Cloud Function), a real infrastructure/billing
     *  change this app doesn't have and wasn't asked for yet. */
    fun observeMyPendingInvites(uid: String): Flow<List<PartyInvite>> = callbackFlow {
        val registration = invitesRef.whereEqualTo("toUid", uid).whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) android.util.Log.e("SocialRepository", "observeMyPendingInvites failed -- check Firestore rules are published", error)
                trySend(snapshot?.documents?.mapNotNull { PartyInvite.fromSnapshot(it) } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    suspend fun respondToInvite(inviteId: String, accept: Boolean): SocialActionResult {
        return try {
            invitesRef.document(inviteId).update("status", if (accept) "accepted" else "rejected").await()
            SocialActionResult.Success
        } catch (e: Exception) {
            SocialActionResult.Error(friendlyFirestoreError(e, "respond to that invite"))
        }
    }
}
