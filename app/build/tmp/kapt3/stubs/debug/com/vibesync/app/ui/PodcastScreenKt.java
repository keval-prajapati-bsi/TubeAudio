package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001a\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u0006\u0006"}, d2 = {"PodcastScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class PodcastScreenKt {
    
    /**
     * Reached via the bottom nav's Menu -> Podcasts entry. Defaults to a "latest podcasts" listing
     * (a canned search, sorted newest-first -- see [com.vibesync.app.extractor.YouTubeSearcher.podcastSearch]
     * for why there's no real "podcasts" category API to pull a true chart from), with its own search
     * box for finding a specific show/episode. Plays through the exact same [SearchResultsList] /
     * [MainViewModel] playback path as every other screen -- podcast episodes are audio-only tracks
     * like anything else in this app, so chapter markers (see `NowPlayingScreen`'s `ChapterRow`) work
     * identically here without any podcast-specific playback code.
     */
    @androidx.compose.runtime.Composable()
    public static final void PodcastScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}