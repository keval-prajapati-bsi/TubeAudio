package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0003\u001a\u000e\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0003\u00a8\u0006\b"}, d2 = {"formatBytes", "", "bytes", "", "formatDuration", "ms", "formatTotalDuration", "totalSeconds", "app_debug"})
public final class FormatKt {
    
    /**
     * "46m" / "1h 12m" -- a playlist header's total-length summary, distinct from [formatDuration]
     * (which is for a single track's elapsed/total time as m:ss).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatTotalDuration(long totalSeconds) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatBytes(long bytes) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatDuration(long ms) {
        return null;
    }
}