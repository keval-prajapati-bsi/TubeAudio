package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\u001a\u008a\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u00052\u0010\b\u0002\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\b2\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0007\u00a8\u0006\u0011"}, d2 = {"YouTubeSearchResultRow", "", "result", "Lcom/vibesync/app/extractor/SearchResult;", "isAdded", "", "isLiked", "onPlay", "Lkotlin/Function0;", "onToggleLike", "onAdd", "onAddToQueue", "isCurrentlyPlaying", "isInQueue", "onWatchVideo", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class YouTubeSearchResultRowKt {
    
    /**
     * Tapping the row plays the song immediately (no save required); the heart saves it to Liked
     * Songs, and "+" opens the full "add to a specific playlist" picker -- three independent
     * actions, matching "play it without needing to add it to the library first".
     */
    @androidx.compose.runtime.Composable()
    public static final void YouTubeSearchResultRow(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.SearchResult result, boolean isAdded, boolean isLiked, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPlay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleLike, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAdd, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddToQueue, boolean isCurrentlyPlaying, boolean isInQueue, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onWatchVideo, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}