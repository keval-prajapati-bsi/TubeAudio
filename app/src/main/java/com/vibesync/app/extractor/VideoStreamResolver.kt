package com.vibesync.app.extractor

import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.VideoStream

/** One selectable option on the video player's resolution picker -- the YouTube-style "different
 *  settings for videos" from the original ask. [isMuxed] streams already contain both video and
 *  audio in a single file ("progressive" delivery, YouTube's simpler/older format) -- in practice
 *  these only go up to about 720p. Higher resolutions are video-only ("adaptive" delivery, what
 *  YouTube's own web player actually uses for 1080p+), so [audioUrl] pairs one with a separately
 *  fetched best-available audio-only stream; [com.vibesync.app.playback.VideoPlayerController]
 *  merges the two back together at playback time via Media3's `MergingMediaSource` (verified via
 *  `javap` on the real media3-exoplayer jar before writing code against it, same discipline this
 *  app already uses for every other unfamiliar API). */
data class VideoQuality(
    val label: String,
    val videoUrl: String,
    val audioUrl: String?,
    val isMuxed: Boolean,
    val width: Int,
    val height: Int
)

data class ResolvedVideo(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    /** Sorted highest resolution first. Never empty -- [resolve] throws instead of returning a
     *  video with nothing playable. */
    val qualities: List<VideoQuality>,
    /** Same YouTube chapter markers [AudioStreamResolver] already surfaces for audio playback --
     *  the video player was missing this entirely even though the underlying data was always
     *  available from the same `StreamExtractor`, just never read here. Most videos have none (an
     *  empty list is the normal case, not an error). */
    val chapters: List<Chapter> = emptyList()
)

class VideoResolutionException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Resolves a video's actual *playable-at-every-resolution* streams -- separate from
 *  [AudioStreamResolver], which only ever wants this app's normal case, the single best
 *  audio-only stream for background listening. Must only be called off the main thread (same
 *  blocking-network/JS-eval caveat as [AudioStreamResolver.resolve]). */
object VideoStreamResolver {
    fun resolve(rawUrl: String): ResolvedVideo {
        val videoId = AudioStreamResolver.extractVideoId(rawUrl)
            ?: throw VideoResolutionException("That doesn't look like a valid YouTube link.")
        try {
            val canonicalUrl = "https://www.youtube.com/watch?v=$videoId"
            val extractor = ServiceList.YouTube.getStreamExtractor(canonicalUrl)
            extractor.fetchPage()

            val bestAudio = extractor.audioStreams
                .filter { it.isUrl && !it.content.isNullOrBlank() }
                .maxByOrNull { it.averageBitrate }

            val muxed = extractor.videoStreams
                .filter { it.isUrl && !it.content.isNullOrBlank() }
                .map { it.toQuality(isMuxed = true, audioUrl = null) }

            // Adaptive (video-only) qualities need a paired audio stream to actually be playable
            // -- if this video somehow has none, adaptive options are simply left out rather than
            // offering a silent-video quality in the picker.
            val adaptive = if (bestAudio != null) {
                extractor.videoOnlyStreams
                    .filter { it.isUrl && !it.content.isNullOrBlank() }
                    .map { it.toQuality(isMuxed = false, audioUrl = bestAudio.content) }
            } else {
                emptyList()
            }

            // Muxed entries come first, so a resolution offered both ways keeps the simpler
            // single-file muxed version (no merge needed) rather than the adaptive one.
            val qualities = (muxed + adaptive)
                .filter { it.height > 0 }
                .distinctBy { it.height }
                .sortedByDescending { it.height }

            if (qualities.isEmpty()) {
                throw VideoResolutionException("This video has no playable video stream available.")
            }

            val chapters = try {
                extractor.streamSegments.map { Chapter(title = it.title, startTimeSeconds = it.startTimeSeconds) }
            } catch (e: Exception) {
                emptyList()
            }

            return ResolvedVideo(
                videoId = videoId,
                title = extractor.name,
                thumbnailUrl = extractor.thumbnails.maxByOrNull { it.height }?.url,
                durationSeconds = extractor.length,
                qualities = qualities,
                chapters = chapters
            )
        } catch (e: VideoResolutionException) {
            throw e
        } catch (e: Exception) {
            throw VideoResolutionException("Couldn't load this video (${e.message ?: e::class.simpleName}).", e)
        }
    }

    private fun VideoStream.toQuality(isMuxed: Boolean, audioUrl: String?): VideoQuality {
        val parsedHeight = height.takeIf { it > 0 } ?: resolution.filter { it.isDigit() }.toIntOrNull() ?: 0
        return VideoQuality(
            label = resolution.ifBlank { "${parsedHeight}p" },
            videoUrl = content,
            audioUrl = audioUrl,
            isMuxed = isMuxed,
            width = width,
            height = parsedHeight
        )
    }
}
