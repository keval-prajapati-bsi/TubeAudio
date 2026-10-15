package com.vibesync.app.data.party

import com.google.firebase.firestore.DocumentSnapshot

/**
 * The party's live state: which track is "now playing" and exactly when it started, so any
 * client (including one joining mid-song) can compute the correct current position without the
 * host having to broadcast a position update every second. See [com.vibesync.app.playback.PartySyncController]
 * for how [playbackStartedAtMillis]/[startPositionMs] turn into an actual seek position.
 *
 * Deliberately hand-mapped from a [DocumentSnapshot] rather than `toObject()`, since
 * [playbackStartedAtMillis]/[createdAtMillis] are written as Firestore server [com.google.firebase.Timestamp]
 * values but read back as plain millis here -- `toObject()`'s reflection-based mapping doesn't
 * reconcile that type difference and would throw at read time.
 */
data class PartyDoc(
    val code: String,
    val hostUid: String,
    val hostName: String,
    val name: String,
    val currentVideoId: String?,
    val currentTitle: String?,
    val currentThumbnailUrl: String?,
    val currentUrl: String?,
    val currentDurationSeconds: Long,
    /** The queue item (if any) that [currentVideoId] corresponds to -- null for an ad-hoc play
     *  never added to the queue. This is what lets "skip next"/"go back" know which queue item to
     *  transition, and lets the UI tell "now playing" apart from "up next" without a 4th status. */
    val currentQueueItemId: String?,
    val isPlaying: Boolean,
    /** The track position, in ms, at the instant [playbackStartedAtMillis] was written. */
    val startPositionMs: Long,
    val playbackStartedAtMillis: Long,
    val createdAtMillis: Long
) {
    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): PartyDoc? {
            if (!doc.exists()) return null
            return PartyDoc(
                code = doc.id,
                hostUid = doc.getString("hostUid") ?: "",
                hostName = doc.getString("hostName") ?: "",
                name = doc.getString("name") ?: "",
                currentVideoId = doc.getString("currentVideoId"),
                currentTitle = doc.getString("currentTitle"),
                currentThumbnailUrl = doc.getString("currentThumbnailUrl"),
                currentUrl = doc.getString("currentUrl"),
                currentDurationSeconds = doc.getLong("currentDurationSeconds") ?: 0L,
                currentQueueItemId = doc.getString("currentQueueItemId"),
                isPlaying = doc.getBoolean("isPlaying") ?: false,
                startPositionMs = doc.getLong("startPositionMs") ?: 0L,
                playbackStartedAtMillis = doc.getTimestamp("playbackStartedAt")?.toDate()?.time ?: 0L,
                createdAtMillis = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }
    }
}

/** No separate "playing" status -- whether an APPROVED item is "up next" or "now playing" is
 *  determined by comparing its id against [PartyDoc.currentQueueItemId], not by its own status
 *  field. This keeps "go to next" / "go back" / "jump to this song" all boil down to the same two
 *  writes (retire the old current item to PLAYED, if any; point the party doc at the new one)
 *  instead of needing a 4-state machine. */
enum class QueueItemStatus { PENDING, APPROVED, PLAYED }

data class PartyQueueItem(
    val id: String,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String?,
    val url: String,
    val durationSeconds: Long,
    val addedByUid: String,
    val addedByName: String,
    val status: QueueItemStatus,
    val position: Long,
    val addedAtMillis: Long,
    /** Only meaningful once [status] is PLAYED -- when "go back" needs to find the *most
     *  recently* played track, not just any played track. */
    val playedAtMillis: Long
) {
    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): PartyQueueItem? {
            val videoId = doc.getString("videoId") ?: return null
            return PartyQueueItem(
                id = doc.id,
                videoId = videoId,
                title = doc.getString("title") ?: "",
                thumbnailUrl = doc.getString("thumbnailUrl"),
                url = doc.getString("url") ?: "",
                durationSeconds = doc.getLong("durationSeconds") ?: 0L,
                addedByUid = doc.getString("addedByUid") ?: "",
                addedByName = doc.getString("addedByName") ?: "",
                status = when (doc.getString("status")) {
                    "approved" -> QueueItemStatus.APPROVED
                    "played" -> QueueItemStatus.PLAYED
                    else -> QueueItemStatus.PENDING
                },
                position = doc.getLong("position") ?: 0L,
                addedAtMillis = doc.getTimestamp("addedAt")?.toDate()?.time ?: 0L,
                playedAtMillis = doc.getTimestamp("playedAt")?.toDate()?.time ?: 0L
            )
        }
    }
}

data class PartyMember(
    val uid: String,
    val displayName: String,
    val joinedAtMillis: Long
) {
    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): PartyMember? {
            return PartyMember(
                uid = doc.id,
                displayName = doc.getString("displayName") ?: "",
                joinedAtMillis = doc.getTimestamp("joinedAt")?.toDate()?.time ?: 0L
            )
        }
    }
}
