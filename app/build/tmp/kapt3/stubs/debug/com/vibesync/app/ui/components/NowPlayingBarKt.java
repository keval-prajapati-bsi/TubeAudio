package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\u001aR\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a\u001e\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0012\u0010\r\u001a\u00020\u00012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0003\u00a8\u0006\u0010"}, d2 = {"NowPlayingBar", "", "state", "Lcom/vibesync/app/playback/PlaybackUiState;", "onTogglePlayPause", "Lkotlin/Function0;", "onSkipNext", "onSkipPrevious", "onExpand", "modifier", "Landroidx/compose/ui/Modifier;", "PlayPauseButton", "onToggle", "Thumbnail", "url", "", "app_debug"})
public final class NowPlayingBarKt {
    
    /**
     * Docked bar: art + title/artist + a single play/pause button, no slider, no prev/next -- a
     * floating glass card (spec: "Floating card, rounded corners") that now rides the same dark
     * Elevated-Card layer as every other raised surface, rather than the older frozen
     * light-theme-always capsule -- that freeze made sense against the old two-tone palette, but the
     * whole point of this pass is one consistent premium dark system, so it's a deliberate reversal
     * of that earlier call, not an oversight. Full transport (prev/next, scrubber, shuffle, repeat)
     * is one tap away via [onExpand]. [onSkipNext]/[onSkipPrevious] stay in the signature so callers
     * don't need to change, just unused here now.
     */
    @androidx.compose.runtime.Composable()
    public static final void NowPlayingBar(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.playback.PlaybackUiState state, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTogglePlayPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSkipNext, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSkipPrevious, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExpand, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Thumbnail(java.lang.String url) {
    }
    
    /**
     * A solid filled circle, not a bare icon -- the play/pause button is the single most-tapped
     * control in the app, and a filled treatment (the convention every major music app's mini
     * player uses) gives it real visual weight.
     */
    @androidx.compose.runtime.Composable()
    private static final void PlayPauseButton(com.vibesync.app.playback.PlaybackUiState state, kotlin.jvm.functions.Function0<kotlin.Unit> onToggle) {
    }
}