package com.vibesync.app.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.vibesync.app.data.db.TrackEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class ImportedTrack(
    val videoId: String,
    val url: String,
    val title: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long
)

data class ImportedPlaylist(val name: String, val tracks: List<ImportedTrack>)

/**
 * Playlists export/import as a small portable JSON file: links + metadata only,
 * never the resolved stream URL (those expire within hours) or downloaded audio
 * bytes -- keeps files tiny and importing never needs a network call up front.
 */
object PlaylistPortability {

    fun exportToShareableUri(context: Context, playlistName: String, tracks: List<TrackEntity>): Uri {
        val json = JSONObject().apply {
            put("playlistName", playlistName)
            put(
                "tracks",
                JSONArray().apply {
                    tracks.forEach { track ->
                        put(
                            JSONObject().apply {
                                put("videoId", track.videoId)
                                put("url", track.url)
                                put("title", track.title)
                                put("thumbnailUrl", track.thumbnailUrl ?: "")
                                put("durationSeconds", track.durationSeconds)
                            }
                        )
                    }
                }
            )
        }

        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = playlistName.replace(Regex("[^a-zA-Z0-9-_]"), "_").ifBlank { "playlist" }
        val file = File(exportsDir, "$safeName.tubeaudio.json")
        file.writeText(json.toString(2))

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun importFromUri(context: Context, uri: Uri): ImportedPlaylist {
        val text = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
            ?: throw IllegalArgumentException("Couldn't read that file.")
        val json = JSONObject(text)
        val name = json.optString("playlistName").ifBlank { "Imported playlist" }
        val tracksJson = json.getJSONArray("tracks")
        val tracks = buildList {
            for (i in 0 until tracksJson.length()) {
                val obj = tracksJson.getJSONObject(i)
                add(
                    ImportedTrack(
                        videoId = obj.getString("videoId"),
                        url = obj.getString("url"),
                        title = obj.getString("title"),
                        thumbnailUrl = obj.optString("thumbnailUrl").ifBlank { null },
                        durationSeconds = obj.optLong("durationSeconds")
                    )
                )
            }
        }
        if (tracks.isEmpty()) throw IllegalArgumentException("That file doesn't have any songs in it.")
        return ImportedPlaylist(name, tracks)
    }
}
