package com.vibesync.app.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.vibesync.app.extractor.USER_AGENT
import com.vibesync.app.extractor.VideoQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoPlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentQuality: VideoQuality? = null,
    val errorMessage: String? = null
)

/**
 * A deliberately separate, self-contained video player -- NOT a extension of the app's existing
 * [PlayerController], which is Application-scoped, MediaSession-backed, and deeply wired into the
 * queue/party-sync/downloads/background-playback system that only ever made sense for audio. Video
 * playback here is screen-scoped instead: a plain [ExoPlayer] created when `VideoPlayerScreen`
 * opens and released when it closes, with no background/lock-screen presence -- watching a video
 * is an active foreground activity, unlike this app's normal audio-while-doing-something-else use
 * case. This is exactly the "second playback path" that was the stated reason full video playback
 * was scoped out of earlier rounds; keeping it fully separate is what makes it safe to add without
 * touching the audio player's already-complex state machine at all.
 */
class VideoPlayerController(context: Context) {
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
            /* handleAudioFocus = */ true
        )
        .build()

    // Same "YouTube's CDN can reject a missing/generic User-Agent" fix already applied to audio
    // streaming/downloads (see PlaybackService/TrackDownloader) -- video streams are resolved by
    // the same NewPipeExtractor, so they need the same UA to actually load.
    private val dataSourceFactory = DefaultDataSource.Factory(
        context.applicationContext,
        DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
    )

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var tickerJob: Job? = null

    init {
        tickerJob = scope.launch {
            while (true) {
                delay(500)
                if (exoPlayer.isPlaying) {
                    _uiState.value = _uiState.value.copy(positionMs = exoPlayer.currentPosition.coerceAtLeast(0))
                }
            }
        }
        exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _uiState.value = _uiState.value.copy(isBuffering = playbackState == androidx.media3.common.Player.STATE_BUFFERING)
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    _uiState.value = _uiState.value.copy(durationMs = exoPlayer.duration.coerceAtLeast(0))
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _uiState.value = _uiState.value.copy(
                    isBuffering = false,
                    errorMessage = "Playback error: ${error.message ?: "stream failed"}"
                )
            }
        })
    }

    /** Switches to [quality], preserving playback position across the switch (so tapping a
     *  different resolution mid-video resumes from the same spot instead of restarting) unless
     *  [startPositionMs] is explicitly given (first load). */
    fun play(quality: VideoQuality, startPositionMs: Long? = null) {
        val resumeAt = startPositionMs ?: exoPlayer.currentPosition.coerceAtLeast(0)
        val wasPlaying = exoPlayer.isPlaying || startPositionMs != null
        _uiState.value = _uiState.value.copy(isBuffering = true, currentQuality = quality, errorMessage = null)

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(quality.videoUrl))
        val source: MediaSource = if (quality.isMuxed || quality.audioUrl == null) {
            videoSource
        } else {
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(quality.audioUrl))
            MergingMediaSource(videoSource, audioSource)
        }

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        if (resumeAt > 0) exoPlayer.seekTo(resumeAt)
        exoPlayer.playWhenReady = wasPlaying
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _uiState.value = _uiState.value.copy(positionMs = positionMs)
    }

    fun currentPositionMs(): Long = exoPlayer.currentPosition.coerceAtLeast(0)

    fun release() {
        tickerJob?.cancel()
        exoPlayer.release()
    }
}
