package com.vibesync.app.data

import android.content.Context
import com.vibesync.app.data.db.TrackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val FIELD_DELIM = "|~|"
private const val ENTRY_DELIM = "|~~|"
private const val MAX_HISTORY = 30

/** A plain "songs you've actually played" list -- the Queue screen's "History" tab. Deliberately
 *  independent of the library/liked/downloaded tables: this needs to work for ad-hoc plays too
 *  (a search/trending result played directly, never saved), so it stores its own lightweight copy
 *  of the metadata needed to show/replay a row (title/thumbnail/duration), same delimited-string
 *  approach as [SearchHistoryPreferences] rather than a Room table -- no schema/migration needed
 *  for what's fundamentally a capped recency list. */
class PlaybackHistoryPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("playback_history", Context.MODE_PRIVATE)

    private val _history = MutableStateFlow(readHistory())
    val history: StateFlow<List<TrackEntity>> = _history.asStateFlow()

    /** Called whenever a track actually starts playing. Most-recent-first, de-duplicated (a
     *  replayed song jumps back to the top rather than appearing twice). */
    fun recordPlay(track: TrackEntity) {
        val updated = (listOf(track) + _history.value.filter { it.videoId != track.videoId }).take(MAX_HISTORY)
        _history.value = updated
        persist(updated)
    }

    private fun persist(list: List<TrackEntity>) {
        val serialized = list.joinToString(ENTRY_DELIM) { track ->
            listOf(track.videoId, track.title, track.thumbnailUrl ?: "", track.durationSeconds.toString())
                .joinToString(FIELD_DELIM)
        }
        prefs.edit().putString(KEY_HISTORY, serialized).apply()
    }

    private fun readHistory(): List<TrackEntity> {
        val raw = prefs.getString(KEY_HISTORY, null)
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(ENTRY_DELIM).mapNotNull { entry ->
            val parts = entry.split(FIELD_DELIM)
            if (parts.size < 4) return@mapNotNull null
            val videoId = parts[0]
            TrackEntity(
                videoId = videoId,
                url = "https://www.youtube.com/watch?v=$videoId",
                title = parts[1],
                thumbnailUrl = parts[2].ifBlank { null },
                durationSeconds = parts[3].toLongOrNull() ?: 0L,
                addedAtEpochMillis = System.currentTimeMillis()
            )
        }
    }

    private companion object {
        const val KEY_HISTORY = "history"
    }
}
