package com.vibesync.app.extractor;

/**
 * Searches YouTube by title via NewPipeExtractor -- same "no official API" approach as
 * [AudioStreamResolver], just for discovery instead of resolving a link you already have.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\bJ\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\bJ\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\f\u001a\u00020\rJ\u0018\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004*\b\u0012\u0004\u0012\u00020\u000f0\u0004H\u0002\u00a8\u0006\u0010"}, d2 = {"Lcom/vibesync/app/extractor/YouTubeSearcher;", "", "()V", "allTrending", "", "Lcom/vibesync/app/extractor/SearchResult;", "podcastSearch", "query", "", "search", "contentFilter", "trending", "filter", "Lcom/vibesync/app/extractor/TrendingFilter;", "toSearchResults", "Lorg/schabi/newpipe/extractor/InfoItem;", "app_debug"})
public final class YouTubeSearcher {
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.extractor.YouTubeSearcher INSTANCE = null;
    
    private YouTubeSearcher() {
        super();
    }
    
    /**
     * [contentFilter] defaults to the "Songs" category (music-only results, this app's original
     * and still most common use case); [podcastSearch] passes the general "Videos" filter
     * instead, since podcast episodes aren't music and the Songs filter would exclude them.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.SearchResult> search(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    java.lang.String contentFilter) {
        return null;
    }
    
    /**
     * Trending tab's "Top charts" listing for a specific filter card. YouTube's general
     * "Trending" kiosk mixes in every content category (gaming, news, movie trailers...), not
     * just music, and kiosk items don't reliably carry a duration -- so instead of that kiosk,
     * this reuses the exact same [search] path (already filtered to the "Songs" category,
     * already returning real durations) with the filter's representative query, then re-sorts
     * client-side per [TrendingFilter.sort] so "Top Hits" is actually ordered by upload date and
     * the popularity-oriented filters are actually ordered by view count, instead of both just
     * reflecting YouTube's generic search-relevance order. A result missing upload-date metadata
     * (see [SearchResult.uploadDateEpochMillis]) sorts to the back for [TrendingSort.LATEST],
     * rather than being dropped -- still worth showing, just without a reliable position.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.SearchResult> trending(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.TrendingFilter filter) {
        return null;
    }
    
    /**
     * The Trending tab's main, always-on "Trending songs" list -- not affected by which "Top
     * charts" card was tapped (tapping one now navigates into its own filtered list instead of
     * changing this one). Same representative-query approximation as [trending].
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.SearchResult> allTrending() {
        return null;
    }
    
    /**
     * Podcast search -- the general "Videos" filter (not "Songs"), sorted newest-first since
     * "the latest podcasts, date-wise" was the actual ask, not YouTube's generic relevance order.
     * A blank [query] falls back to a canned "podcast" search for the Podcasts tab's default
     * listing, same "representative query, not a real category API" approximation this app
     * already uses for Trending -- there's no dedicated "podcasts" search filter in
     * NewPipeExtractor/YouTube's own search API, verified via `YoutubeSearchQueryHandlerFactory`'s
     * actual available content filters before assuming one existed.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.SearchResult> podcastSearch(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    private final java.util.List<com.vibesync.app.extractor.SearchResult> toSearchResults(java.util.List<? extends org.schabi.newpipe.extractor.InfoItem> $this$toSearchResults) {
        return null;
    }
}