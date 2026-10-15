package com.vibesync.app;

/**
 * Manual, Application-scoped dependency holder (no DI framework, matching the
 * other in-house Android apps). [playerController] deliberately outlives any
 * single Activity/ViewModel so audio keeps playing after the UI is destroyed.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b6\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0019\u001a\u00020\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u001d\u001a\u00020\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010!\u001a\u00020\"\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010%\u001a\u00020&\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010)\u001a\u00020*\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0011\u0010-\u001a\u00020.\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u00101\u001a\u000202\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u00105\u001a\u000206\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00108R\u0011\u00109\u001a\u00020:\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u0011\u0010=\u001a\u00020>\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010@R\u0011\u0010A\u001a\u00020B\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0011\u0010E\u001a\u00020F\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010HR\u0011\u0010I\u001a\u00020J\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010LR\u0011\u0010M\u001a\u00020N\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u0010PR\u0011\u0010Q\u001a\u00020R\u00a2\u0006\b\n\u0000\u001a\u0004\bS\u0010T\u00a8\u0006U"}, d2 = {"Lcom/vibesync/app/AppContainer;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "authRepository", "Lcom/vibesync/app/data/auth/AuthRepository;", "getAuthRepository", "()Lcom/vibesync/app/data/auth/AuthRepository;", "database", "Lcom/vibesync/app/data/db/AppDatabase;", "downloadHttpClient", "Lokhttp3/OkHttpClient;", "downloadLocationPreferences", "Lcom/vibesync/app/data/DownloadLocationPreferences;", "getDownloadLocationPreferences", "()Lcom/vibesync/app/data/DownloadLocationPreferences;", "equalizerController", "Lcom/vibesync/app/playback/EqualizerController;", "getEqualizerController", "()Lcom/vibesync/app/playback/EqualizerController;", "equalizerPreferences", "Lcom/vibesync/app/data/EqualizerPreferences;", "getEqualizerPreferences", "()Lcom/vibesync/app/data/EqualizerPreferences;", "libraryRepository", "Lcom/vibesync/app/data/LibraryRepository;", "getLibraryRepository", "()Lcom/vibesync/app/data/LibraryRepository;", "localPartySyncController", "Lcom/vibesync/app/playback/LocalPartySyncController;", "getLocalPartySyncController", "()Lcom/vibesync/app/playback/LocalPartySyncController;", "lyricsPreferences", "Lcom/vibesync/app/data/LyricsPreferences;", "getLyricsPreferences", "()Lcom/vibesync/app/data/LyricsPreferences;", "lyricsRepository", "Lcom/vibesync/app/lyrics/LyricsRepository;", "getLyricsRepository", "()Lcom/vibesync/app/lyrics/LyricsRepository;", "networkMonitor", "Lcom/vibesync/app/data/NetworkMonitor;", "getNetworkMonitor", "()Lcom/vibesync/app/data/NetworkMonitor;", "partyRepository", "Lcom/vibesync/app/data/party/PartyRepository;", "getPartyRepository", "()Lcom/vibesync/app/data/party/PartyRepository;", "partySyncController", "Lcom/vibesync/app/playback/PartySyncController;", "getPartySyncController", "()Lcom/vibesync/app/playback/PartySyncController;", "playbackHistoryPreferences", "Lcom/vibesync/app/data/PlaybackHistoryPreferences;", "getPlaybackHistoryPreferences", "()Lcom/vibesync/app/data/PlaybackHistoryPreferences;", "playerController", "Lcom/vibesync/app/playback/PlayerController;", "getPlayerController", "()Lcom/vibesync/app/playback/PlayerController;", "profilePreferences", "Lcom/vibesync/app/data/ProfilePreferences;", "getProfilePreferences", "()Lcom/vibesync/app/data/ProfilePreferences;", "recommendationPreferences", "Lcom/vibesync/app/data/RecommendationPreferences;", "getRecommendationPreferences", "()Lcom/vibesync/app/data/RecommendationPreferences;", "searchHistoryPreferences", "Lcom/vibesync/app/data/SearchHistoryPreferences;", "getSearchHistoryPreferences", "()Lcom/vibesync/app/data/SearchHistoryPreferences;", "socialRepository", "Lcom/vibesync/app/data/social/SocialRepository;", "getSocialRepository", "()Lcom/vibesync/app/data/social/SocialRepository;", "themePreferences", "Lcom/vibesync/app/data/ThemePreferences;", "getThemePreferences", "()Lcom/vibesync/app/data/ThemePreferences;", "trackDownloader", "Lcom/vibesync/app/download/TrackDownloader;", "getTrackDownloader", "()Lcom/vibesync/app/download/TrackDownloader;", "app_debug"})
public final class AppContainer {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.AppDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.LibraryRepository libraryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.ThemePreferences themePreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.SearchHistoryPreferences searchHistoryPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.ProfilePreferences profilePreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.RecommendationPreferences recommendationPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.DownloadLocationPreferences downloadLocationPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.NetworkMonitor networkMonitor = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.PlaybackHistoryPreferences playbackHistoryPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.EqualizerPreferences equalizerPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.EqualizerController equalizerController = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.LyricsPreferences lyricsPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient downloadHttpClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.download.TrackDownloader trackDownloader = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.lyrics.LyricsRepository lyricsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.PlayerController playerController = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.auth.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.party.PartyRepository partyRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.PartySyncController partySyncController = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.social.SocialRepository socialRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.LocalPartySyncController localPartySyncController = null;
    
    public AppContainer(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.LibraryRepository getLibraryRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.ThemePreferences getThemePreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.SearchHistoryPreferences getSearchHistoryPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.ProfilePreferences getProfilePreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.RecommendationPreferences getRecommendationPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.DownloadLocationPreferences getDownloadLocationPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.NetworkMonitor getNetworkMonitor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.PlaybackHistoryPreferences getPlaybackHistoryPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.EqualizerPreferences getEqualizerPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.playback.EqualizerController getEqualizerController() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.LyricsPreferences getLyricsPreferences() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.download.TrackDownloader getTrackDownloader() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.lyrics.LyricsRepository getLyricsRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.playback.PlayerController getPlayerController() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.auth.AuthRepository getAuthRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.party.PartyRepository getPartyRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.playback.PartySyncController getPartySyncController() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.social.SocialRepository getSocialRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.playback.LocalPartySyncController getLocalPartySyncController() {
        return null;
    }
}