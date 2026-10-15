package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a<\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0003\u001a&\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u00a8\u0006\u0011"}, d2 = {"VideoChapterRow", "", "chapters", "", "Lcom/vibesync/app/extractor/Chapter;", "positionMs", "", "onJumpTo", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "VideoPlayerScreen", "videoUrl", "", "title", "onClose", "Lkotlin/Function0;", "app_debug"})
public final class VideoPlayerScreenKt {
    
    /**
     * Full-screen video playback with a YouTube-style resolution picker -- the part of the original
     * podcast ask deliberately deferred to its own round, now built for real. Uses a self-contained
     * [VideoPlayerController] (its own plain ExoPlayer, not the app's shared audio [PlayerController])
     * so nothing about this screen can disturb normal audio-only playback state; callers are
     * responsible for pausing the audio player before opening this (see
     * [MainViewModel.openVideoPlayer]) so the two don't play over each other.
     */
    @androidx.compose.runtime.Composable()
    public static final void VideoPlayerScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String videoUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    /**
     * Same chip-row concept as the audio player's chapter markers (see `NowPlayingScreen.kt`'s
     * `ChapterRow`) -- video playback was missing this entirely even though the underlying YouTube
     * chapter data was always available, it just wasn't being read here. Styled for the dark video
     * overlay rather than reusing that private composable directly.
     */
    @androidx.compose.runtime.Composable()
    private static final void VideoChapterRow(java.util.List<com.vibesync.app.extractor.Chapter> chapters, long positionMs, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onJumpTo, androidx.compose.ui.Modifier modifier) {
    }
}