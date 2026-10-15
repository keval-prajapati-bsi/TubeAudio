package com.vibesync.app

import android.content.Context
import com.vibesync.app.data.DownloadLocationPreferences
import com.vibesync.app.data.EqualizerPreferences
import com.vibesync.app.data.LibraryRepository
import com.vibesync.app.data.LyricsPreferences
import com.vibesync.app.data.NetworkMonitor
import com.vibesync.app.data.PlaybackHistoryPreferences
import com.vibesync.app.data.ProfilePreferences
import com.vibesync.app.data.RecommendationPreferences
import com.vibesync.app.data.SearchHistoryPreferences
import com.vibesync.app.data.ThemePreferences
import com.vibesync.app.data.auth.AuthRepository
import com.vibesync.app.data.db.AppDatabase
import com.vibesync.app.data.party.PartyRepository
import com.vibesync.app.data.social.SocialRepository
import com.vibesync.app.download.TrackDownloader
import com.vibesync.app.lyrics.LyricsRepository
import com.vibesync.app.playback.EqualizerController
import com.vibesync.app.playback.LocalPartySyncController
import com.vibesync.app.playback.PartySyncController
import com.vibesync.app.playback.PlayerController
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Manual, Application-scoped dependency holder (no DI framework, matching the
 * other in-house Android apps). [playerController] deliberately outlives any
 * single Activity/ViewModel so audio keeps playing after the UI is destroyed.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val libraryRepository = LibraryRepository(database)
    val themePreferences = ThemePreferences(context)
    val searchHistoryPreferences = SearchHistoryPreferences(context)
    val profilePreferences = ProfilePreferences(context)
    val recommendationPreferences = RecommendationPreferences(context)
    val downloadLocationPreferences = DownloadLocationPreferences(context)
    val networkMonitor = NetworkMonitor(context)
    val playbackHistoryPreferences = PlaybackHistoryPreferences(context)
    val equalizerPreferences = EqualizerPreferences(context)
    val equalizerController = EqualizerController(equalizerPreferences)
    val lyricsPreferences = LyricsPreferences(context)

    private val downloadHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    val trackDownloader = TrackDownloader(context, downloadHttpClient, libraryRepository, downloadLocationPreferences)
    val lyricsRepository = LyricsRepository(downloadHttpClient, lyricsPreferences)

    val playerController = PlayerController(context, libraryRepository, database.playbackStateDao(), playbackHistoryPreferences)

    val authRepository = AuthRepository()
    val partyRepository = PartyRepository()
    val partySyncController = PartySyncController(partyRepository, playerController)
    val socialRepository = SocialRepository()
    val localPartySyncController = LocalPartySyncController(playerController)
}
