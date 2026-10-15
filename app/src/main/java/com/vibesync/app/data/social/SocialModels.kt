package com.vibesync.app.data.social

import com.google.firebase.firestore.DocumentSnapshot

/** Online/offline is approximated from a heartbeat, not real presence -- this app has no Realtime
 *  Database (the product Firebase's `onDisconnect()` actually needs), only Firestore, so there's
 *  no reliable signal for "this device just lost its connection." Instead every signed-in device
 *  periodically bumps its own `users/{uid}.lastSeenAt`, and anyone reading it treats "seen within
 *  the last [ONLINE_WINDOW_MILLIS]" as online -- a disclosed approximation, same honesty pattern
 *  as this app's other "no real backend for X" caveats (Trending's canned queries, Popular
 *  artists' fixed list). A killed app / lost connection shows as online for up to that window,
 *  then quietly goes stale. */
const val ONLINE_WINDOW_MILLIS = 90_000L

data class UserProfile(
    val uid: String,
    val displayName: String,
    val username: String?,
    val lastSeenAtMillis: Long?
) {
    val isOnline: Boolean
        get() = lastSeenAtMillis != null && (System.currentTimeMillis() - lastSeenAtMillis) < ONLINE_WINDOW_MILLIS

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): UserProfile? {
            if (!doc.exists()) return null
            return UserProfile(
                uid = doc.id,
                displayName = doc.getString("displayName") ?: "",
                username = doc.getString("username"),
                lastSeenAtMillis = doc.getTimestamp("lastSeenAt")?.toDate()?.time
            )
        }
    }
}

data class Friend(
    val uid: String,
    val displayName: String,
    val username: String?,
    val lastSeenAtMillis: Long?
) {
    val isOnline: Boolean
        get() = lastSeenAtMillis != null && (System.currentTimeMillis() - lastSeenAtMillis) < ONLINE_WINDOW_MILLIS

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): Friend? {
            return Friend(
                uid = doc.id,
                displayName = doc.getString("displayName") ?: "",
                username = doc.getString("username"),
                lastSeenAtMillis = doc.getTimestamp("lastSeenAt")?.toDate()?.time
            )
        }
    }
}

enum class InviteStatus { PENDING, ACCEPTED, REJECTED }

data class PartyInvite(
    val id: String,
    val fromUid: String,
    val fromDisplayName: String,
    val toUid: String,
    val partyCode: String,
    val partyName: String,
    val status: InviteStatus,
    val createdAtMillis: Long
) {
    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): PartyInvite? {
            val partyCode = doc.getString("partyCode") ?: return null
            return PartyInvite(
                id = doc.id,
                fromUid = doc.getString("fromUid") ?: "",
                fromDisplayName = doc.getString("fromDisplayName") ?: "",
                toUid = doc.getString("toUid") ?: "",
                partyCode = partyCode,
                partyName = doc.getString("partyName") ?: "",
                status = when (doc.getString("status")) {
                    "accepted" -> InviteStatus.ACCEPTED
                    "rejected" -> InviteStatus.REJECTED
                    else -> InviteStatus.PENDING
                },
                createdAtMillis = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }
    }
}
