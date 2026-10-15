package com.vibesync.app.data;

/**
 * First-launch "what do you like to listen to?" picks -- languages and moods/genres, used to
 * build the Home screen's "Recommended" search query (see [buildRecommendedQuery]). Plain
 * SharedPreferences: a couple of small string sets and a completed flag, no need for Room/
 * DataStore.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0017\u001a\u00020\nJ\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u0019\u001a\u00020\nH\u0002J\"\u0010\u001a\u001a\u00020\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\n0\tJ\u0006\u0010\u001e\u001a\u00020\u001bR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000f\u00a8\u0006 "}, d2 = {"Lcom/vibesync/app/data/RecommendationPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_onboardingCompleted", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_selectedGenres", "", "", "_selectedLanguages", "onboardingCompleted", "Lkotlinx/coroutines/flow/StateFlow;", "getOnboardingCompleted", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "selectedGenres", "getSelectedGenres", "selectedLanguages", "getSelectedLanguages", "buildRecommendedQuery", "readSet", "key", "savePreferences", "", "languages", "genres", "skipOnboarding", "Companion", "app_debug"})
public final class RecommendationPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _onboardingCompleted = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> onboardingCompleted = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Set<java.lang.String>> _selectedLanguages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> selectedLanguages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Set<java.lang.String>> _selectedGenres = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> selectedGenres = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_COMPLETED = "onboarding_completed";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_LANGUAGES = "languages";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_GENRES = "genres";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.RecommendationPreferences.Companion Companion = null;
    
    public RecommendationPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getOnboardingCompleted() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getSelectedLanguages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getSelectedGenres() {
        return null;
    }
    
    /**
     * Called both on first-launch onboarding and later edits from the Profile screen -- either
     * way it (re)marks onboarding as done.
     */
    public final void savePreferences(@org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> languages, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> genres) {
    }
    
    public final void skipOnboarding() {
    }
    
    /**
     * No preferences set (skipped, or nothing picked) falls back to a general trending query
     * rather than an empty/broken search.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String buildRecommendedQuery() {
        return null;
    }
    
    private final java.util.Set<java.lang.String> readSet(java.lang.String key) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/vibesync/app/data/RecommendationPreferences$Companion;", "", "()V", "KEY_COMPLETED", "", "KEY_GENRES", "KEY_LANGUAGES", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}