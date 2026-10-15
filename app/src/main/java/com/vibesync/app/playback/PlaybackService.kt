package com.vibesync.app.playback

import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vibesync.app.MainActivity
import com.vibesync.app.TubeAudioApplication
import com.vibesync.app.extractor.USER_AGENT

/**
 * Keeps ExoPlayer + MediaSession alive as a foreground service so audio keeps
 * playing after the app is closed or the screen is locked, and exposes
 * lock-screen / notification transport controls (both handled automatically
 * by Media3 once a session is published).
 */
class PlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // YouTube's CDN (googlevideo.com) can reject a resolved stream URL fetched with a
        // generic/missing User-Agent, so streaming needs the same UA NewPipeExtractor used to
        // resolve it. DefaultDataSource still falls back to FileDataSource for file:// URIs
        // (downloaded/offline tracks), only the http(s) path gets the custom UA.
        val httpDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
        val dataSourceFactory = DefaultDataSource.Factory(this, httpDataSourceFactory)

        val exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
        exoPlayer.setWakeMode(C.WAKE_MODE_NETWORK)

        // A fixed session id (rather than ExoPlayer's own lazily-assigned one) so the Equalizer
        // can attach to it immediately, before any track has actually started playing.
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val audioSessionId = audioManager.generateAudioSessionId()
        exoPlayer.setAudioSessionId(audioSessionId)
        (application as TubeAudioApplication).container.equalizerController.attach(audioSessionId)

        player = exoPlayer

        val sessionActivityIntent = Intent(this, MainActivity::class.java)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val currentPlayer = player
        if (currentPlayer == null || !currentPlayer.playWhenReady || currentPlayer.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        (application as TubeAudioApplication).container.equalizerController.release()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        player = null
        super.onDestroy()
    }
}
