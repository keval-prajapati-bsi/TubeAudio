package com.vibesync.app.lyrics

import com.vibesync.app.data.LyricsPreferences
import com.vibesync.app.data.db.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

sealed class LyricsResult {
    data class Found(val text: String) : LyricsResult()
    object NotFound : LyricsResult()
    data class Error(val message: String) : LyricsResult()
}

/**
 * Fetches plain-text lyrics from lrclib.net -- a free, no-API-key-required, openly-licensed
 * community lyrics database (unlike scraping a site like Genius, which would be a ToS violation;
 * lrclib is explicitly built for programmatic access). Standing caveat, same spirit as the
 * NewPipeExtractor one: this is a new external network dependency this app didn't have before,
 * worth knowing about if lyrics ever come back wrong or a build needs to work fully offline.
 *
 * Matching is best-effort: this app only has a track's title (YouTube video titles are often
 * "Artist - Song (Official Video)"-shaped, not cleanly split into separate fields), so this
 * searches by title alone and takes the first non-instrumental result with real lyrics text --
 * it does not attempt fuzzy artist/title parsing.
 */
class LyricsRepository(
    private val httpClient: OkHttpClient,
    private val preferences: LyricsPreferences
) {
    suspend fun fetchLyrics(track: TrackEntity): LyricsResult = withContext(Dispatchers.IO) {
        if (preferences.isCachedNotFound(track.videoId)) return@withContext LyricsResult.NotFound
        preferences.get(track.videoId)?.let { return@withContext LyricsResult.Found(it) }

        try {
            val query = URLEncoder.encode(track.title, "UTF-8")
            val request = Request.Builder()
                .url("https://lrclib.net/api/search?track_name=$query")
                .header("User-Agent", "TubeAudio (personal Android app, no API key)")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext LyricsResult.Error("Lyrics service returned HTTP ${response.code}")
                }
                val body = response.body?.string()
                if (body.isNullOrBlank()) return@withContext LyricsResult.NotFound

                val results = JSONArray(body)
                for (i in 0 until results.length()) {
                    val entry = results.getJSONObject(i)
                    val instrumental = entry.optBoolean("instrumental", false)
                    val plainLyrics = entry.optString("plainLyrics", "")
                    if (!instrumental && plainLyrics.isNotBlank()) {
                        preferences.put(track.videoId, plainLyrics)
                        return@withContext LyricsResult.Found(plainLyrics)
                    }
                }
                preferences.markNotFound(track.videoId)
                LyricsResult.NotFound
            }
        } catch (e: Exception) {
            LyricsResult.Error(e.message ?: "Couldn't reach the lyrics service")
        }
    }
}
