package com.vibesync.app.data;

/**
 * A minimal, dependency-free crash reporter: installs a global uncaught-exception handler that
 * writes the full stack trace to a local file before letting the crash proceed exactly as it
 * normally would (the process still dies -- this only makes sure the *reason* survives the
 * crash instead of vanishing with it). Built because there's no way to pull a real `adb logcat`
 * or Play Console crash report for this app right now -- the fallback is [ui.LastCrashScreen],
 * which reads this file back on next launch so the exact stack trace can be read/copied directly
 * off the phone, no computer needed.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\n\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\bJ \u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/vibesync/app/data/CrashLogger;", "", "()V", "FILE_NAME", "", "clear", "", "context", "Landroid/content/Context;", "install", "readLastCrash", "writeCrash", "thread", "Ljava/lang/Thread;", "throwable", "", "app_debug"})
public final class CrashLogger {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FILE_NAME = "last_crash.txt";
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.data.CrashLogger INSTANCE = null;
    
    private CrashLogger() {
        super();
    }
    
    public final void install(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    private final void writeCrash(android.content.Context context, java.lang.Thread thread, java.lang.Throwable throwable) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String readLastCrash(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void clear(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}