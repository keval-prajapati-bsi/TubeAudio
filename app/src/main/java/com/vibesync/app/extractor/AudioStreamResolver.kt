package com.vibesync.app.extractor

import org.schabi.newpipe.extractor.ServiceList
import java.util.regex.Pattern

/** One of YouTube's own chapter markers ("sections between videos" you can jump between) --
 *  verified via `StreamExtractor.getStreamSegments(): List<StreamSegment>` on the actual
 *  NewPipeExtractor jar before relying on it. Most videos don't have any (an empty list is the
 *  normal case, not an error). */
data class Chapter(val title: String, val startTimeSeconds: Int)

data class ResolvedAudio(
    val videoId: String,
    val canonicalUrl: String,
    val title: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val streamUrl: String,
    /** The stream's *real* container format extension (e.g. "m4a", "webm") -- YouTube's
     *  audio-only streams are AAC-in-M4A or Opus-in-WebM in practice, never actual MP3, so a
     *  downloaded file is named with this rather than a hardcoded/misleading extension. Falls
     *  back to "m4a" (the common case) if NewPipeExtractor doesn't report a format. */
    val fileExtension: String,
    val chapters: List<Chapter> = emptyList()
)

class AudioResolutionException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Resolves a pasted YouTube link into a direct, playable audio-only stream URL
 * via NewPipeExtractor. Must only be called off the main thread -- it performs
 * blocking network calls and, for ciphered streams, JS evaluation.
 */
object AudioStreamResolver {

    private val videoIdPattern = Pattern.compile(
        "(?:youtube(?:-nocookie)?\\.com/(?:watch\\?(?:.*&)?v=|shorts/|embed/|live/)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    )
    private val bareIdPattern = Regex("^[a-zA-Z0-9_-]{11}$")

    fun extractVideoId(rawUrl: String): String? {
        val trimmed = rawUrl.trim()
        val matcher = videoIdPattern.matcher(trimmed)
        if (matcher.find()) return matcher.group(1)
        if (bareIdPattern.matches(trimmed)) return trimmed
        return null
    }

    fun resolve(rawUrl: String): ResolvedAudio {
        val videoId = extractVideoId(rawUrl)
            ?: throw AudioResolutionException("That doesn't look like a valid YouTube link.")

        try {
            val canonicalUrl = "https://www.youtube.com/watch?v=$videoId"
            val extractor = ServiceList.YouTube.getStreamExtractor(canonicalUrl)
            extractor.fetchPage()

            val bestAudio = extractor.audioStreams
                .filter { !it.content.isNullOrBlank() }
                .maxByOrNull { it.averageBitrate }
                ?: throw AudioResolutionException("This video has no audio-only stream available.")

            val thumbnailUrl = extractor.thumbnails.maxByOrNull { it.height }?.url
            // Not every video has chapters -- an empty list from a failed/absent parse is the
            // normal case, so this is swallowed rather than failing the whole resolve over it.
            val chapters = try {
                extractor.streamSegments.map { Chapter(title = it.title, startTimeSeconds = it.startTimeSeconds) }
            } catch (e: Exception) {
                emptyList()
            }

            return ResolvedAudio(
                videoId = videoId,
                canonicalUrl = canonicalUrl,
                title = extractor.name,
                thumbnailUrl = thumbnailUrl,
                durationSeconds = extractor.length,
                streamUrl = bestAudio.content,
                fileExtension = bestAudio.format?.suffix ?: "m4a",
                chapters = chapters
            )
        } catch (e: AudioResolutionException) {
            throw e
        } catch (e: Exception) {
            throw AudioResolutionException(
                "Couldn't load this video (${e.message ?: e::class.simpleName}).",
                e
            )
        }
    }
}
