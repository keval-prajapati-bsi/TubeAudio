package com.vibesync.app.extractor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0007\u001a\u00020\b*\u00020\t\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"TrendingFilters", "", "Lcom/vibesync/app/extractor/TrendingFilter;", "getTrendingFilters", "()Ljava/util/List;", "currentYear", "", "toTransientTrackEntity", "Lcom/vibesync/app/data/db/TrackEntity;", "Lcom/vibesync/app/extractor/SearchResult;", "app_debug"})
public final class YouTubeSearcherKt {
    
    /**
     * Computed live (not hardcoded) so "Top Hits" always tracks the current year without needing
     * a code change every January.
     */
    private static final int currentYear = 0;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.vibesync.app.extractor.TrendingFilter> TrendingFilters = null;
    
    /**
     * A not-yet-saved [TrackEntity] built straight from search-result metadata -- for playing/
     * queuing a result directly without adding it to the library first (see
     * [com.vibesync.app.ui.MainViewModel.playSearchResultDirectly]).
     */
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.data.db.TrackEntity toTransientTrackEntity(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.SearchResult $this$toTransientTrackEntity) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<com.vibesync.app.extractor.TrendingFilter> getTrendingFilters() {
        return null;
    }
}