package com.vibesync.app.data.party

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.vibesync.app.data.db.TrackEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class PartyActionResult {
    object Success : PartyActionResult()
    data class Error(val message: String) : PartyActionResult()
}

sealed class CreatePartyResult {
    data class Success(val code: String) : CreatePartyResult()
    data class Error(val message: String) : CreatePartyResult()
}

sealed class JoinPartyResult {
    object Success : JoinPartyResult()
    object NotFound : JoinPartyResult()
    data class Error(val message: String) : JoinPartyResult()
}

private val PARTY_CODE_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789" // no 0/O/1/I/L -- easy to read aloud

/**
 * Firestore-backed: `parties/{code}` (the code IS the doc id, so "join by code" is a direct doc
 * lookup, never a query -- no Firestore indexes needed anywhere in here by design), with
 * `parties/{code}/queue/{itemId}` and `parties/{code}/members/{uid}` subcollections. See
 * `FIRESTORE_RULES.md` in the project root for the security rules this data model assumes
 * (host-only approve/reorder/track-control/kick, any member can request/leave).
 *
 * Queue items are never deleted once approved -- they transition PENDING -> APPROVED -> PLAYED
 * (see [QueueItemStatus]) instead, which is what makes "go back one song" possible: it's just
 * "find the most recently PLAYED item and make it current again."
 */
class PartyRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val partiesRef = firestore.collection("parties")

    private fun queueRef(code: String) = partiesRef.document(code).collection("queue")
    private fun membersRef(code: String) = partiesRef.document(code).collection("members")

    /** A raw Firestore `PERMISSION_DENIED` is meaningless to a user ("what's a security rule?"),
     *  and it's also the single most common failure mode in this app's whole party feature -- the
     *  rules in `FIRESTORE_RULES.md` have to be manually re-pasted into the Firebase console every
     *  time they change, a step outside this app's own reach, and it's easy to forget one existed.
     *  Surfacing it as an actionable message instead of the raw exception text turns "I can't
     *  remove a user, nothing happens" into something the user can actually act on. */
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

    suspend fun createParty(hostUid: String, hostName: String, partyName: String): CreatePartyResult {
        return try {
            var code = randomCode()
            var attempts = 0
            while (attempts < 5 && partiesRef.document(code).get().await().exists()) {
                code = randomCode()
                attempts++
            }

            partiesRef.document(code).set(
                mapOf(
                    "hostUid" to hostUid,
                    "hostName" to hostName,
                    "name" to partyName.ifBlank { "$hostName's Party" },
                    "isPlaying" to false,
                    "startPositionMs" to 0L,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            membersRef(code).document(hostUid)
                .set(mapOf("displayName" to hostName, "joinedAt" to FieldValue.serverTimestamp()))
                .await()

            CreatePartyResult.Success(code)
        } catch (e: Exception) {
            CreatePartyResult.Error(friendlyFirestoreError(e, "create the party"))
        }
    }

    suspend fun joinParty(code: String, uid: String, displayName: String): JoinPartyResult {
        return try {
            val normalized = code.trim().uppercase()
            val snapshot = partiesRef.document(normalized).get().await()
            if (!snapshot.exists()) return JoinPartyResult.NotFound

            membersRef(normalized).document(uid)
                .set(mapOf("displayName" to displayName, "joinedAt" to FieldValue.serverTimestamp()))
                .await()
            JoinPartyResult.Success
        } catch (e: Exception) {
            JoinPartyResult.Error(friendlyFirestoreError(e, "join the party"))
        }
    }

    fun leaveParty(code: String, uid: String) {
        membersRef(code).document(uid).delete()
    }

    /** Host-only (enforced by security rules): removes someone else's membership. The removed
     *  member's own client detects this via [observeMembers] (their uid disappears from the live
     *  list) and exits the party locally -- see `MainViewModel`'s membership-watch. */
    suspend fun kickMember(code: String, uid: String): PartyActionResult {
        return try {
            membersRef(code).document(uid).delete().await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "remove that member"))
        }
    }

    fun observeParty(code: String): Flow<PartyDoc?> = callbackFlow {
        val registration = partiesRef.document(code).addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("PartyRepository", "observeParty($code) failed -- check Firestore rules are published", error)
            }
            trySend(snapshot?.let { PartyDoc.fromSnapshot(it) })
        }
        awaitClose { registration.remove() }
    }

    /** Unordered on purpose -- ordering by two different fields depending on [QueueItemStatus]
     *  would need a Firestore composite index; sorting the (small) result list client-side avoids
     *  that entirely. Callers split/sort into pending-by-addedAt, approved-by-position, and
     *  played-by-playedAt. */
    fun observeQueue(code: String): Flow<List<PartyQueueItem>> = callbackFlow {
        val registration = queueRef(code).addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("PartyRepository", "observeQueue($code) failed -- check Firestore rules are published", error)
            }
            val items = snapshot?.documents?.mapNotNull { PartyQueueItem.fromSnapshot(it) } ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }

    fun observeMembers(code: String): Flow<List<PartyMember>> = callbackFlow {
        val registration = membersRef(code).addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("PartyRepository", "observeMembers($code) failed -- check Firestore rules are published", error)
            }
            val members = snapshot?.documents?.mapNotNull { PartyMember.fromSnapshot(it) } ?: emptyList()
            trySend(members)
        }
        awaitClose { registration.remove() }
    }

    /** Any party member can request a song -- lands as "pending" until the host approves it.
     *  [autoApprove] is host-only (enforced by security rules, not just this flag) and used when
     *  the host plays something directly or queues up their own playlist.
     *
     *  Refuses a song that's already pending or approved in this party (checked server-side via a
     *  fresh query, not just against whatever the caller has cached, so two people adding the same
     *  song at nearly the same moment still can't both get it in) -- a song that already PLAYED is
     *  fine to re-request, since it's no longer "in the queue." */
    suspend fun addToQueue(
        code: String,
        track: TrackEntity,
        addedByUid: String,
        addedByName: String,
        autoApprove: Boolean,
        position: Long = 0L
    ): PartyActionResult {
        return try {
            if (isAlreadyQueued(code, track.videoId)) {
                return PartyActionResult.Error("\"${track.title}\" is already in the queue.")
            }
            queueRef(code).add(
                mapOf(
                    "videoId" to track.videoId,
                    "title" to track.title,
                    "thumbnailUrl" to track.thumbnailUrl,
                    "url" to track.url,
                    "durationSeconds" to track.durationSeconds,
                    "addedByUid" to addedByUid,
                    "addedByName" to addedByName,
                    "status" to if (autoApprove) "approved" else "pending",
                    "position" to position,
                    "addedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "add that song to the queue"))
        }
    }

    /** Bulk version for "play this whole playlist" -- one batch write instead of N round trips.
     *  Tracks already queued (pending/approved) are silently skipped rather than failing the
     *  whole batch, so requesting a playlist that's 80% already-queued still adds the other 20%. */
    suspend fun addPlaylistToQueue(
        code: String,
        tracks: List<TrackEntity>,
        addedByUid: String,
        addedByName: String,
        autoApprove: Boolean,
        startPosition: Long
    ): PartyActionResult {
        return try {
            val existingIds = queueRef(code).get().await().documents
                .filter { it.getString("status") != "played" }
                .mapNotNull { it.getString("videoId") }
                .toSet()
            val newTracks = tracks.distinctBy { it.videoId }.filterNot { it.videoId in existingIds }
            if (newTracks.isEmpty()) {
                return PartyActionResult.Error("Everything in that playlist is already in the queue.")
            }

            val batch = firestore.batch()
            newTracks.forEachIndexed { index, track ->
                val doc = queueRef(code).document()
                batch.set(
                    doc,
                    mapOf(
                        "videoId" to track.videoId,
                        "title" to track.title,
                        "thumbnailUrl" to track.thumbnailUrl,
                        "url" to track.url,
                        "durationSeconds" to track.durationSeconds,
                        "addedByUid" to addedByUid,
                        "addedByName" to addedByName,
                        "status" to if (autoApprove) "approved" else "pending",
                        "position" to startPosition + index,
                        "addedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "add that playlist to the queue"))
        }
    }

    private suspend fun isAlreadyQueued(code: String, videoId: String): Boolean {
        val matches = queueRef(code).whereEqualTo("videoId", videoId).get().await()
        return matches.documents.any { it.getString("status") != "played" }
    }

    /** Host-only (enforced by security rules): approve a pending request, giving it a real
     *  position at the end of the current approved queue. */
    suspend fun approveQueueItem(code: String, itemId: String, position: Long): PartyActionResult {
        return try {
            queueRef(code).document(itemId).update(mapOf("status" to "approved", "position" to position)).await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "approve that request"))
        }
    }

    /** Only for a still-PENDING request the host doesn't want -- a real delete, unlike a PLAYED
     *  item, which is kept around as history. */
    suspend fun rejectQueueItem(code: String, itemId: String): PartyActionResult {
        return try {
            queueRef(code).document(itemId).delete().await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "remove that request"))
        }
    }

    /** Host-only: a real delete for a still-upcoming (APPROVED, not yet current) queue item --
     *  distinct from [rejectQueueItem] (pending requests) and [markPlayed] (retiring the outgoing
     *  track as history) since this is the host explicitly deciding "actually, not this one" for
     *  something already sitting in "Up next". */
    suspend fun removeQueueItem(code: String, itemId: String): PartyActionResult {
        return try {
            queueRef(code).document(itemId).delete().await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "remove that song"))
        }
    }

    /** Host-only: rewrite every approved item's position in one batch, matching the drag-reorder
     *  pattern already used for local playlists (see `PlaylistTrackDao.reorder()`). */
    suspend fun reorderQueue(code: String, orderedItemIds: List<String>): PartyActionResult {
        return try {
            val batch = firestore.batch()
            orderedItemIds.forEachIndexed { index, itemId ->
                batch.update(queueRef(code).document(itemId), "position", index.toLong())
            }
            batch.commit().await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "reorder the queue"))
        }
    }

    /** Host-only: marks a queue item PLAYED (with a server timestamp, so [playPrevious]-style
     *  "most recently played" lookups are reliable) -- called on the outgoing track whenever the
     *  party moves to a different one. */
    suspend fun markPlayed(code: String, itemId: String): PartyActionResult {
        return try {
            queueRef(code).document(itemId)
                .update(mapOf("status" to "played", "playedAt" to FieldValue.serverTimestamp()))
                .await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "update the queue"))
        }
    }

    /** Host-only: (re-)marks a queue item APPROVED at a given position -- used both for normal
     *  approval and for "go back" pushing the previously-current item back into "Up next". */
    suspend fun markApproved(code: String, itemId: String, position: Long): PartyActionResult {
        return try {
            queueRef(code).document(itemId).update(mapOf("status" to "approved", "position" to position)).await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "update the queue"))
        }
    }

    /** Host-only: sets what's "now playing" and freezes a fresh server timestamp as the sync
     *  anchor -- see [com.vibesync.app.playback.PartySyncController] for how clients turn this
     *  into an actual playback position, including for someone joining mid-song. [queueItemId] is
     *  null for an ad-hoc play that was never added to the queue. */
    suspend fun setCurrentTrack(code: String, track: TrackEntity, queueItemId: String?, startPositionMs: Long): PartyActionResult {
        return try {
            partiesRef.document(code).update(
                mapOf(
                    "currentVideoId" to track.videoId,
                    "currentTitle" to track.title,
                    "currentThumbnailUrl" to track.thumbnailUrl,
                    "currentUrl" to track.url,
                    "currentDurationSeconds" to track.durationSeconds,
                    "currentQueueItemId" to queueItemId,
                    "isPlaying" to true,
                    "startPositionMs" to startPositionMs,
                    "playbackStartedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "update the party's current track"))
        }
    }

    /** Host-only: play/pause/seek all funnel through here -- same write shape either way (freeze
     *  the current position + a fresh server timestamp), since a seek is really just "pause and
     *  immediately resume at a different position" from the sync formula's point of view. */
    suspend fun setPlaybackState(code: String, isPlaying: Boolean, positionMs: Long): PartyActionResult {
        return try {
            partiesRef.document(code).update(
                mapOf(
                    "isPlaying" to isPlaying,
                    "startPositionMs" to positionMs,
                    "playbackStartedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            PartyActionResult.Success
        } catch (e: Exception) {
            PartyActionResult.Error(friendlyFirestoreError(e, "update playback state"))
        }
    }

    private fun randomCode(): String = (1..6).map { PARTY_CODE_CHARS.random() }.joinToString("")
}
