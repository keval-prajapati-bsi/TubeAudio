package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0003\u001a\u001e\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0012\u0010\n\u001a\u00020\u00012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003H\u0003\u00a8\u0006\f"}, d2 = {"LyricsMessage", "", "title", "", "message", "LyricsScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "Lkotlin/Function0;", "LyricsThumbnail", "url", "app_debug"})
public final class LyricsScreenKt {
    
    /**
     * Best-effort lyrics for whatever's currently playing, via lrclib.net (see
     * [com.vibesync.app.lyrics.LyricsRepository] for the matching caveat -- title-only search,
     * no separate artist field to disambiguate with).
     */
    @androidx.compose.runtime.Composable()
    public static final void LyricsScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LyricsMessage(java.lang.String title, java.lang.String message) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LyricsThumbnail(java.lang.String url) {
    }
}