package com.vibesync.app.download

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.vibesync.app.data.DownloadLocationPreferences
import com.vibesync.app.data.LibraryRepository
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.extractor.AudioStreamResolver
import com.vibesync.app.extractor.ResolvedAudio
import com.vibesync.app.extractor.USER_AGENT
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

sealed class DownloadState {
    data class Downloading(val fraction: Float) : DownloadState()
    data class Paused(val fraction: Float) : DownloadState()
}

/**
 * Downloads a track's resolved audio stream so it can be played back with no network at all.
 * By default files live in [Context.getFilesDir] (app-private storage, gone on uninstall); if
 * the user has picked a custom folder via [DownloadLocationPreferences] (Profile -> Download
 * location, a Storage Access Framework tree), new downloads go there instead as a
 * `content://...` file, and [TrackEntity.localFilePath] carries whichever kind of path/URI is
 * actually in use -- everything downstream (playback, deletion) branches on whether that string
 * starts with `content://`.
 *
 * Supports pausing (cancels the job but remembers bytes written and the resolved stream URL, for
 * an HTTP Range-resume within the same app session -- a fresh process restart falls back to
 * starting that track over). Byte-range resume only applies to the default app-private location;
 * a custom SAF folder always restarts a resumed download from scratch, since document providers
 * don't reliably support append-writes the same way a local file does -- a known simplification,
 * same spirit as the existing "pause doesn't survive process death" one.
 */
class TrackDownloader(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val libraryRepository: LibraryRepository,
    private val downloadLocationPreferences: DownloadLocationPreferences
) {
    private val defaultDownloadDir: File = File(context.filesDir, "audio").apply { mkdirs() }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val jobs = mutableMapOf<String, Job>()
    private val bytesWritten = mutableMapOf<String, Long>()
    private val resolvedAudio = mutableMapOf<String, ResolvedAudio>()

    private val _downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadState>> = _downloadStates.asStateFlow()

    /** Tracks currently downloading or paused, keyed by videoId -- lets the Downloads tab show
     *  an in-progress row (with its live progress ring) before the track is actually marked
     *  downloaded in the database. */
    private val _activeDownloadTracks = MutableStateFlow<Map<String, TrackEntity>>(emptyMap())
    val activeDownloadTracks: StateFlow<Map<String, TrackEntity>> = _activeDownloadTracks.asStateFlow()

    private val _downloadErrors = MutableStateFlow<String?>(null)
    val downloadErrors: StateFlow<String?> = _downloadErrors.asStateFlow()

    fun downloadTrack(track: TrackEntity) {
        if (track.isDownloaded || jobs.containsKey(track.videoId)) return
        startDownloadJob(track, resumeFromBytes = 0L)
    }

    fun pauseDownload(videoId: String) {
        val fraction = (_downloadStates.value[videoId] as? DownloadState.Downloading)?.fraction ?: 0f
        jobs[videoId]?.cancel()
        jobs.remove(videoId)
        _downloadStates.update { it + (videoId to DownloadState.Paused(fraction)) }
    }

    fun resumeDownload(track: TrackEntity) {
        startDownloadJob(track, resumeFromBytes = bytesWritten[track.videoId] ?: 0L)
    }

    fun cancelDownload(videoId: String) {
        jobs[videoId]?.cancel()
        jobs.remove(videoId)
        bytesWritten.remove(videoId)
        resolvedAudio.remove(videoId)
        deleteAnyExistingOutput(videoId)
        _downloadStates.update { it - videoId }
        _activeDownloadTracks.update { it - videoId }
    }

    /** Sequential on purpose (one track at a time) to avoid hammering bandwidth/YouTube with
     *  many parallel resolves; waits for each track's job to finish (or pause) before starting
     *  the next. */
    suspend fun downloadPlaylist(tracks: List<TrackEntity>) {
        tracks.forEach { track ->
            if (track.isDownloaded) return@forEach
            downloadTrack(track)
            jobs[track.videoId]?.join()
        }
    }

    suspend fun removeDownload(track: TrackEntity) {
        withContext(Dispatchers.IO) {
            val path = track.localFilePath
            if (path != null) {
                if (path.startsWith("content://")) {
                    DocumentFile.fromSingleUri(context, Uri.parse(path))?.delete()
                } else {
                    File(path).delete()
                }
            }
        }
        libraryRepository.clearDownload(track.videoId)
    }

    fun dismissError() {
        _downloadErrors.value = null
    }

    /** Deletes whatever's on disk for [videoId], regardless of extension -- covers both a
     *  same-session partial download (known extension) and any stale file left over from before
     *  downloads used the stream's real format extension (was always ".audio"). */
    private fun deleteAnyExistingOutput(videoId: String) {
        val treeUri = downloadLocationPreferences.customTreeUri.value
        if (treeUri != null) {
            val dir = DocumentFile.fromTreeUri(context, Uri.parse(treeUri))
            dir?.listFiles()?.filter { it.name?.startsWith("$videoId.") == true }?.forEach { it.delete() }
        } else {
            defaultDownloadDir.listFiles { file -> file.name.startsWith("$videoId.") }?.forEach { it.delete() }
        }
    }

    /** Opens (creating if needed) the output target for [videoId], honoring a custom download
     *  location if one is set, and named with [extension] -- the audio stream's *real* container
     *  format (e.g. "m4a", "webm"; YouTube never actually serves true MP3 audio-only streams, so
     *  this is what makes the saved file playable by other apps instead of a misleading/generic
     *  extension). Returns the identifier to persist as [TrackEntity.localFilePath] alongside the
     *  open [OutputStream] to write into. [resumeFromBytes] is only honored for the default
     *  app-private location -- a custom SAF folder always starts fresh (see class doc). */
    private fun openDownloadTarget(videoId: String, extension: String, resumeFromBytes: Long): Triple<String, OutputStream, Long> {
        val treeUriString = downloadLocationPreferences.customTreeUri.value
        if (treeUriString != null) {
            val dir = DocumentFile.fromTreeUri(context, Uri.parse(treeUriString))
                ?: error("Can't access the chosen download folder anymore.")
            val fileName = "$videoId.$extension"
            dir.findFile(fileName)?.delete()
            val doc = dir.createFile(mimeTypeForExtension(extension), fileName)
                ?: error("Couldn't create a file in the chosen download folder.")
            val stream = context.contentResolver.openOutputStream(doc.uri, "w")
                ?: error("Couldn't open the chosen download folder for writing.")
            return Triple(doc.uri.toString(), stream, 0L)
        }
        val file = File(defaultDownloadDir, "$videoId.$extension")
        val isResuming = resumeFromBytes > 0
        return Triple(file.absolutePath, FileOutputStream(file, isResuming), resumeFromBytes)
    }

    private fun mimeTypeForExtension(extension: String): String = when (extension.lowercase()) {
        "m4a", "mp4" -> "audio/mp4"
        "webm" -> "audio/webm"
        "mp3" -> "audio/mpeg"
        "opus", "ogg" -> "audio/ogg"
        else -> "application/octet-stream"
    }

    private fun startDownloadJob(track: TrackEntity, resumeFromBytes: Long) {
        val initialFraction = (_downloadStates.value[track.videoId] as? DownloadState.Paused)?.fraction ?: 0f
        _downloadStates.update { it + (track.videoId to DownloadState.Downloading(initialFraction)) }
        _activeDownloadTracks.update { it + (track.videoId to track) }

        val job = scope.launch {
            try {
                val resolved = resolvedAudio.getOrPut(track.videoId) {
                    withContext(Dispatchers.IO) { AudioStreamResolver.resolve(track.url) }
                }
                val streamUrl = resolved.streamUrl

                val savedPath = withContext(Dispatchers.IO) {
                    val (outputPath, output, effectiveResumeFrom) =
                        openDownloadTarget(track.videoId, resolved.fileExtension, resumeFromBytes)
                    val isResumed = effectiveResumeFrom > 0

                    val requestBuilder = Request.Builder().url(streamUrl).header("User-Agent", USER_AGENT)
                    if (isResumed) requestBuilder.header("Range", "bytes=$effectiveResumeFrom-")

                    httpClient.newCall(requestBuilder.build()).execute().use { response ->
                        if (!response.isSuccessful) error("Download failed (HTTP ${response.code})")
                        val body = response.body ?: error("Empty download response")
                        val rangeHonored = response.code == 206
                        val totalBytes = if (rangeHonored) {
                            parseTotalFromContentRange(response.header("Content-Range"))
                                ?: (body.contentLength() + effectiveResumeFrom)
                        } else {
                            body.contentLength()
                        }
                        var written = if (rangeHonored) effectiveResumeFrom else 0L

                        output.use { out ->
                            body.byteStream().use { input ->
                                val buffer = ByteArray(8 * 1024)
                                while (true) {
                                    ensureActive()
                                    val read = input.read(buffer)
                                    if (read == -1) break
                                    out.write(buffer, 0, read)
                                    written += read
                                    bytesWritten[track.videoId] = written
                                    if (totalBytes > 0) {
                                        val fraction = (written.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                                        _downloadStates.update { it + (track.videoId to DownloadState.Downloading(fraction)) }
                                    }
                                }
                            }
                        }
                    }
                    outputPath
                }

                libraryRepository.markDownloaded(track.videoId, savedPath)
                _downloadStates.update { it - track.videoId }
                _activeDownloadTracks.update { it - track.videoId }
                bytesWritten.remove(track.videoId)
                resolvedAudio.remove(track.videoId)
            } catch (e: CancellationException) {
                // Paused (pauseDownload already recorded the Paused state, and deliberately
                // leaves this track in activeDownloadTracks) or cancelled (cancelDownload
                // already cleared everything) -- nothing more to do here.
                throw e
            } catch (e: Exception) {
                _downloadErrors.value = "Couldn't download \"${track.title}\" (${e.message ?: e::class.simpleName})."
                _downloadStates.update { it - track.videoId }
                _activeDownloadTracks.update { it - track.videoId }
                bytesWritten.remove(track.videoId)
                resolvedAudio.remove(track.videoId)
            } finally {
                jobs.remove(track.videoId)
            }
        }
        jobs[track.videoId] = job
    }

    private fun parseTotalFromContentRange(header: String?): Long? {
        val slashIndex = header?.lastIndexOf('/') ?: return null
        if (slashIndex < 0) return null
        return header.substring(slashIndex + 1).toLongOrNull()
    }
}
