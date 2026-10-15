package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0016\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u0007\u001a$\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u000f\u0010\u0010\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0004\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0005\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0011"}, d2 = {"LogoMagenta", "Landroidx/compose/ui/graphics/Color;", "J", "LogoVioletDark", "LogoVioletLight", "SplashBlack", "SplashScreen", "", "onFinished", "Lkotlin/Function0;", "VibeSyncLogoMark", "size", "Landroidx/compose/ui/unit/Dp;", "modifier", "Landroidx/compose/ui/Modifier;", "VibeSyncLogoMark-D5KLDUw", "(FLandroidx/compose/ui/Modifier;)V", "app_debug"})
public final class SplashScreenKt {
    private static final long SplashBlack = 0L;
    private static final long LogoVioletLight = 0L;
    private static final long LogoVioletDark = 0L;
    private static final long LogoMagenta = 0L;
    
    /**
     * Shown once on cold start, before [TubeAudioApp] -- a VibeSync-branded animated intro (logo
     * mark scales/fades in, wordmark follows) rather than the Android 12+ system SplashScreen API,
     * which only supports a static icon and can't give the "loading animation" that was asked for.
     * Deliberately theme-invariant (fixed near-black background) rather than following the user's
     * light/dark app theme -- this is a brand moment, not app UI. Only shown once per process (not
     * replayed on rotation) -- see [MainActivity]'s `SplashState` object.
     *
     * The logo mark is a single clean glyph -- a gradient ring plus a violet-to-magenta disc with a
     * centered play triangle, no secondary icon competing for attention -- deliberately simplified
     * from an earlier version that also overlaid a separate music-note badge, which read as cluttered
     * at actual launcher-icon size; this same mark is what `ic_launcher_foreground.xml` uses too, so
     * the home-screen icon and this splash are visually the same logo, not two different ones.
     */
    @androidx.compose.runtime.Composable()
    public static final void SplashScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFinished) {
    }
}