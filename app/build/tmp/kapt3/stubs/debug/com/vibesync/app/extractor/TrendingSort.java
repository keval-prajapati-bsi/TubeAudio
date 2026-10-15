package com.vibesync.app.extractor;

/**
 * How a [TrendingFilter]'s raw search results get ordered -- YouTube's own search relevance
 * ranking doesn't actually correspond to "newest" or "most viewed", so a filter that's meant to
 * represent either of those re-sorts client-side using the real metadata NewPipeExtractor
 * reports, rather than trusting search-result order to mean what its label says.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/vibesync/app/extractor/TrendingSort;", "", "(Ljava/lang/String;I)V", "RELEVANCE", "LATEST", "MOST_VIEWED", "app_debug"})
public enum TrendingSort {
    /*public static final*/ RELEVANCE /* = new RELEVANCE() */,
    /*public static final*/ LATEST /* = new LATEST() */,
    /*public static final*/ MOST_VIEWED /* = new MOST_VIEWED() */;
    
    TrendingSort() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.vibesync.app.extractor.TrendingSort> getEntries() {
        return null;
    }
}