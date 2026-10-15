package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u001a\u001a\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a&\u0010\u000e\u001a\u00020\t2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\t0\u0012H\u0003\u001aC\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u00172\b\b\u0002\u0010\f\u001a\u00020\r2\u0011\u0010\u0018\u001a\r\u0012\u0004\u0012\u00020\t0\u0017\u00a2\u0006\u0002\b\u0019H\u0003\u001a\u0016\u0010\u001a\u001a\u00020\t2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\u0017H\u0003\u001a&\u0010\u001c\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u0017H\u0003\u001a\u001e\u0010\u001f\u001a\u00020\t2\u0006\u0010 \u001a\u00020!2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u0017H\u0003\u001a2\u0010\"\u001a\u00020\t2\u0006\u0010#\u001a\u00020$2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020!0\u00062\u0012\u0010&\u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\t0\u0012H\u0003\u001a(\u0010\'\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\u00102\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\t0\u00172\b\b\u0002\u0010)\u001a\u00020\u0010H\u0003\u001a\b\u0010*\u001a\u00020$H\u0002\u001a\u0010\u0010+\u001a\u00020\u00102\u0006\u0010#\u001a\u00020$H\u0002\u001a\u0016\u0010,\u001a\b\u0012\u0004\u0012\u00020!0\u00062\u0006\u0010#\u001a\u00020$H\u0002\u001a\u0010\u0010-\u001a\u00020\u00102\u0006\u0010#\u001a\u00020$H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0004\"\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"DASHBOARD_PLAYLIST_LIMIT", "", "HomeCardWidth", "Landroidx/compose/ui/unit/Dp;", "F", "HomeFilterChips", "", "Lcom/vibesync/app/ui/HomeFilterChip;", "DashboardScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "modifier", "Landroidx/compose/ui/Modifier;", "HomeFilterChipsRow", "selectedLabel", "", "onSelect", "Lkotlin/Function1;", "HomeShelfCard", "title", "subtitle", "onClick", "Lkotlin/Function0;", "art", "Landroidx/compose/runtime/Composable;", "OfflineCard", "onGoToDownloads", "RecentlyPlayedTile", "playlist", "Lcom/vibesync/app/data/db/PlaylistEntity;", "SeasonMoodCard", "mood", "Lcom/vibesync/app/ui/SeasonMood;", "SeasonMoodShelf", "season", "Lcom/vibesync/app/ui/Season;", "moods", "onMoodClick", "SectionHeader", "onSeeAll", "seeAllLabel", "currentSeason", "seasonEyebrow", "seasonMoods", "seasonTitle", "app_debug"})
public final class DashboardScreenKt {
    private static final int DASHBOARD_PLAYLIST_LIMIT = 5;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.vibesync.app.ui.HomeFilterChip> HomeFilterChips = null;
    private static final float HomeCardWidth = 0.0F;
    
    private static final com.vibesync.app.ui.Season currentSeason() {
        return null;
    }
    
    private static final java.lang.String seasonEyebrow(com.vibesync.app.ui.Season season) {
        return null;
    }
    
    private static final java.lang.String seasonTitle(com.vibesync.app.ui.Season season) {
        return null;
    }
    
    private static final java.util.List<com.vibesync.app.ui.SeasonMood> seasonMoods(com.vibesync.app.ui.Season season) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HomeFilterChipsRow(java.lang.String selectedLabel, kotlin.jvm.functions.Function1<? super com.vibesync.app.ui.HomeFilterChip, kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionHeader(java.lang.String title, kotlin.jvm.functions.Function0<kotlin.Unit> onSeeAll, java.lang.String seeAllLabel) {
    }
    
    /**
     * The one section on Home that's a row of cards side-by-side, never a vertical list -- per
     * explicit direction, since this reads as "a handful of moods to pick from" rather than "a
     * queue of items to scan", the same distinction the mood/season cards in the reference mockup
     * drew against Quick Picks' list-style rows.
     */
    @androidx.compose.runtime.Composable()
    private static final void SeasonMoodShelf(com.vibesync.app.ui.Season season, java.util.List<com.vibesync.app.ui.SeasonMood> moods, kotlin.jvm.functions.Function1<? super com.vibesync.app.ui.SeasonMood, kotlin.Unit> onMoodClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SeasonMoodCard(com.vibesync.app.ui.SeasonMood mood, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * Shared card shape for every Home shelf below the season cards -- Recently Played, Recommended
     * for you, and Your Playlists all render through this one composable now, at the same fixed
     * width, so the three sections read as one consistent card language instead of three different
     * treatments (a flat-color tile row, a cover-art row, and a plain list).
     */
    @androidx.compose.runtime.Composable()
    private static final void HomeShelfCard(java.lang.String title, java.lang.String subtitle, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> art) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OfflineCard(kotlin.jvm.functions.Function0<kotlin.Unit> onGoToDownloads) {
    }
    
    /**
     * A flat colored tile (playlist's own theme color) with a small centered vinyl icon, rendered
     * through the shared [HomeShelfCard] so it's the same size as every other Home shelf card.
     */
    @androidx.compose.runtime.Composable()
    private static final void RecentlyPlayedTile(com.vibesync.app.data.db.PlaylistEntity playlist, com.vibesync.app.ui.MainViewModel viewModel, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}