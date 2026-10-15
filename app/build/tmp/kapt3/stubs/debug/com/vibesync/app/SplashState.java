package com.vibesync.app;

/**
 * Survives Activity recreation (a plain object, not Activity/ViewModel state) -- see
 * [MainActivity.onCreate]'s splash-gating for why this exists: without it, rotating the phone
 * while the app was already past the splash would still replay it, since `remember{}` state
 * inside `setContent{}` normally resets whenever the Activity is recreated.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/vibesync/app/SplashState;", "", "()V", "hasShown", "", "getHasShown", "()Z", "setHasShown", "(Z)V", "app_debug"})
final class SplashState {
    private static boolean hasShown = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.SplashState INSTANCE = null;
    
    private SplashState() {
        super();
    }
    
    public final boolean getHasShown() {
        return false;
    }
    
    public final void setHasShown(boolean p0) {
    }
}