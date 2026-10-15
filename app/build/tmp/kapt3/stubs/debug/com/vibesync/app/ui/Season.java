package com.vibesync.app.ui;

/**
 * Season detection for the Home page's opening shelf. Deliberately just the device's own
 * calendar month, no location permission or weather API -- same "approximate, don't fake a real
 * data source" honesty as [SearchScreen]'s genre tiles and Trending's charts. The four buckets
 * below are the Northern-hemisphere / North Indian calendar (this app's own content skews
 * Bollywood/Hindi), so this would need a real hemisphere or locale check before it'd make sense
 * for a Southern-hemisphere user -- flagged here, not solved, since that's a bigger call than a
 * Home-screen shelf should make silently.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/vibesync/app/ui/Season;", "", "(Ljava/lang/String;I)V", "WINTER", "SUMMER", "MONSOON", "POST_MONSOON", "app_debug"})
enum Season {
    /*public static final*/ WINTER /* = new WINTER() */,
    /*public static final*/ SUMMER /* = new SUMMER() */,
    /*public static final*/ MONSOON /* = new MONSOON() */,
    /*public static final*/ POST_MONSOON /* = new POST_MONSOON() */;
    
    Season() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.vibesync.app.ui.Season> getEntries() {
        return null;
    }
}