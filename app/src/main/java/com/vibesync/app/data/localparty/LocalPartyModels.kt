package com.vibesync.app.data.localparty

import org.json.JSONArray
import org.json.JSONObject

data class LocalMember(val id: String, val displayName: String, val isHost: Boolean)

/** No PENDING/APPROVED/PLAYED status like the Firebase Party's queue -- a deliberate v1
 *  simplification for the local room: anyone can add a song and it lands straight in the queue
 *  (host can still remove/reorder it, same as the Firebase party's "Up next"), and a played song
 *  is just dropped rather than kept as browsable history. This is a real, disclosed scope
 *  reduction versus the Firebase party, not an oversight -- see version27/CHANGES.txt. */
data class LocalQueueItem(
    val id: String,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String?,
    val url: String,
    val durationSeconds: Long,
    val addedByName: String
)

/** The host's single source of truth, broadcast in full to every connected guest on every change
 *  (see [com.vibesync.app.localparty.LocalPartyHost]) -- unlike the Firebase party's Firestore
 *  documents (many small targeted writes), a raw socket has no partial-update/diff concept of its
 *  own, so sending the whole snapshot each time is the simplest correct approach at LAN-party
 *  scale (a handful of phones, a short queue). */
data class LocalRoomState(
    val roomName: String,
    val members: List<LocalMember> = emptyList(),
    val queue: List<LocalQueueItem> = emptyList(),
    val currentVideoId: String? = null,
    val currentTitle: String? = null,
    val currentThumbnailUrl: String? = null,
    val currentUrl: String? = null,
    val currentDurationSeconds: Long = 0L,
    val isPlaying: Boolean = false,
    /** Same expected-position formula as the Firebase party (see `PartySyncController`):
     *  `startPositionMs + (now - playbackStartedAtMillis)` while playing. Both ends of the socket
     *  share the same wall-clock assumption the Firebase version already relies on (no clock-skew
     *  handshake) -- same disclosed simplification, not new to the local room. */
    val startPositionMs: Long = 0L,
    val playbackStartedAtMillis: Long = 0L
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("roomName", roomName)
        put("members", JSONArray(members.map {
            JSONObject().apply { put("id", it.id); put("displayName", it.displayName); put("isHost", it.isHost) }
        }))
        put("queue", JSONArray(queue.map {
            JSONObject().apply {
                put("id", it.id); put("videoId", it.videoId); put("title", it.title)
                put("thumbnailUrl", it.thumbnailUrl); put("url", it.url)
                put("durationSeconds", it.durationSeconds); put("addedByName", it.addedByName)
            }
        }))
        put("currentVideoId", currentVideoId)
        put("currentTitle", currentTitle)
        put("currentThumbnailUrl", currentThumbnailUrl)
        put("currentUrl", currentUrl)
        put("currentDurationSeconds", currentDurationSeconds)
        put("isPlaying", isPlaying)
        put("startPositionMs", startPositionMs)
        put("playbackStartedAtMillis", playbackStartedAtMillis)
    }

    companion object {
        fun fromJson(json: JSONObject): LocalRoomState {
            val members = (0 until json.optJSONArray("members")!!.length()).map { i ->
                val m = json.getJSONArray("members").getJSONObject(i)
                LocalMember(m.getString("id"), m.getString("displayName"), m.getBoolean("isHost"))
            }
            val queue = (0 until json.optJSONArray("queue")!!.length()).map { i ->
                val q = json.getJSONArray("queue").getJSONObject(i)
                LocalQueueItem(
                    id = q.getString("id"),
                    videoId = q.getString("videoId"),
                    title = q.getString("title"),
                    thumbnailUrl = q.optString("thumbnailUrl").takeIf { it.isNotEmpty() },
                    url = q.getString("url"),
                    durationSeconds = q.getLong("durationSeconds"),
                    addedByName = q.getString("addedByName")
                )
            }
            return LocalRoomState(
                roomName = json.getString("roomName"),
                members = members,
                queue = queue,
                currentVideoId = json.optString("currentVideoId").takeIf { it.isNotEmpty() },
                currentTitle = json.optString("currentTitle").takeIf { it.isNotEmpty() },
                currentThumbnailUrl = json.optString("currentThumbnailUrl").takeIf { it.isNotEmpty() },
                currentUrl = json.optString("currentUrl").takeIf { it.isNotEmpty() },
                currentDurationSeconds = json.optLong("currentDurationSeconds", 0L),
                isPlaying = json.optBoolean("isPlaying", false),
                startPositionMs = json.optLong("startPositionMs", 0L),
                playbackStartedAtMillis = json.optLong("playbackStartedAtMillis", 0L)
            )
        }
    }
}

/** A room found via NSD discovery, before actually connecting to it. */
data class DiscoveredRoom(val name: String, val host: String, val port: Int)
