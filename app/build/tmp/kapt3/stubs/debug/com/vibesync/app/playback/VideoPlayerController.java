package com.vibesync.app.playback;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0016\u001a\u00020\u0017J\u001f\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u0017\u00a2\u0006\u0002\u0010\u001dJ\u0006\u0010\u001e\u001a\u00020\u0019J\u000e\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u0017J\u0006\u0010!\u001a\u00020\u0019R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006\""}, d2 = {"Lcom/vibesync/app/playback/VideoPlayerController;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/vibesync/app/playback/VideoPlayerUiState;", "dataSourceFactory", "Landroidx/media3/datasource/DefaultDataSource$Factory;", "exoPlayer", "Landroidx/media3/exoplayer/ExoPlayer;", "getExoPlayer", "()Landroidx/media3/exoplayer/ExoPlayer;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "tickerJob", "Lkotlinx/coroutines/Job;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "currentPositionMs", "", "play", "", "quality", "Lcom/vibesync/app/extractor/VideoQuality;", "startPositionMs", "(Lcom/vibesync/app/extractor/VideoQuality;Ljava/lang/Long;)V", "release", "seekTo", "positionMs", "togglePlayPause", "app_debug"})
public final class VideoPlayerController {
    @org.jetbrains.annotations.NotNull()
    private final androidx.media3.exoplayer.ExoPlayer exoPlayer = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.media3.datasource.DefaultDataSource.Factory dataSourceFactory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.playback.VideoPlayerUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.VideoPlayerUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job tickerJob;
    
    public VideoPlayerController(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.media3.exoplayer.ExoPlayer getExoPlayer() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.VideoPlayerUiState> getUiState() {
        return null;
    }
    
    /**
     * Switches to [quality], preserving playback position across the switch (so tapping a
     * different resolution mid-video resumes from the same spot instead of restarting) unless
     * [startPositionMs] is explicitly given (first load).
     */
    public final void play(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.VideoQuality quality, @org.jetbrains.annotations.Nullable()
    java.lang.Long startPositionMs) {
    }
    
    public final void togglePlayPause() {
    }
    
    public final void seekTo(long positionMs) {
    }
    
    public final long currentPositionMs() {
        return 0L;
    }
    
    public final void release() {
    }
}