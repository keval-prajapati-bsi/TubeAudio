package com.vibesync.app.data;

/**
 * A simple cache of fetched lyrics text keyed by videoId (see
 * [com.vibesync.app.lyrics.LyricsRepository]) so re-opening the Lyrics tab for a song you've
 * already looked up doesn't refetch from the network every time. No cap/eviction -- personal-
 * app scale, and each entry is only a few KB of text.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\tJ\u0010\u0010\r\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\tJ\u0016\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\tR\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/vibesync/app/data/LyricsPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "get", "", "videoId", "isCachedNotFound", "", "key", "markNotFound", "", "put", "lyrics", "Companion", "app_debug"})
public final class LyricsPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String NOT_FOUND_MARKER = "\u0000NOT_FOUND";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.LyricsPreferences.Companion Companion = null;
    
    public LyricsPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String get(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
        return null;
    }
    
    public final void put(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String lyrics) {
    }
    
    /**
     * A cached "we looked, there weren't any" -- distinct from never-fetched, so a repeat visit
     * doesn't hit the network again for a song with no lyrics available.
     */
    public final void markNotFound(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final boolean isCachedNotFound(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
        return false;
    }
    
    private final java.lang.String key(java.lang.String videoId) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/vibesync/app/data/LyricsPreferences$Companion;", "", "()V", "NOT_FOUND_MARKER", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}