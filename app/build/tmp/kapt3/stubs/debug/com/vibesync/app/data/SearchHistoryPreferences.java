package com.vibesync.app.data;

/**
 * Recent YouTube song-search queries, newest first -- plain SharedPreferences, same
 * reasoning as [ThemePreferences] (one small piece of state, not worth DataStore).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\bJ\u0006\u0010\u0013\u001a\u00020\u0011J\u000e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002J\u000e\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\bJ\u0016\u0010\u0016\u001a\u00020\u00112\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0019"}, d2 = {"Lcom/vibesync/app/data/SearchHistoryPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_recentQueries", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "recentQueries", "Lkotlinx/coroutines/flow/StateFlow;", "getRecentQueries", "()Lkotlinx/coroutines/flow/StateFlow;", "addQuery", "", "query", "clear", "loadQueries", "removeQuery", "save", "queries", "Companion", "app_debug"})
public final class SearchHistoryPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<java.lang.String>> _recentQueries = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> recentQueries = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY = "recent_queries";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.SearchHistoryPreferences.Companion Companion = null;
    
    public SearchHistoryPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> getRecentQueries() {
        return null;
    }
    
    public final void addQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void removeQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void clear() {
    }
    
    private final void save(java.util.List<java.lang.String> queries) {
    }
    
    private final java.util.List<java.lang.String> loadQueries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/vibesync/app/data/SearchHistoryPreferences$Companion;", "", "()V", "KEY", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}