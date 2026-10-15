package com.vibesync.app.data;

/**
 * A plain "songs you've actually played" list -- the Queue screen's "History" tab. Deliberately
 * independent of the library/liked/downloaded tables: this needs to work for ad-hoc plays too
 * (a search/trending result played directly, never saved), so it stores its own lightweight copy
 * of the metadata needed to show/replay a row (title/thumbnail/duration), same delimited-string
 * approach as [SearchHistoryPreferences] rather than a Room table -- no schema/migration needed
 * for what's fundamentally a capped recency list.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002J\u000e\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\bR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/vibesync/app/data/PlaybackHistoryPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_history", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/vibesync/app/data/db/TrackEntity;", "history", "Lkotlinx/coroutines/flow/StateFlow;", "getHistory", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "persist", "", "list", "readHistory", "recordPlay", "track", "Companion", "app_debug"})
public final class PlaybackHistoryPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> _history = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> history = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_HISTORY = "history";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.PlaybackHistoryPreferences.Companion Companion = null;
    
    public PlaybackHistoryPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> getHistory() {
        return null;
    }
    
    /**
     * Called whenever a track actually starts playing. Most-recent-first, de-duplicated (a
     * replayed song jumps back to the top rather than appearing twice).
     */
    public final void recordPlay(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track) {
    }
    
    private final void persist(java.util.List<com.vibesync.app.data.db.TrackEntity> list) {
    }
    
    private final java.util.List<com.vibesync.app.data.db.TrackEntity> readHistory() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/vibesync/app/data/PlaybackHistoryPreferences$Companion;", "", "()V", "KEY_HISTORY", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}