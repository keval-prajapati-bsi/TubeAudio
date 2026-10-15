package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\u001a:\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003\u001a\u001a\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001a\u001e\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003\u00a8\u0006\u0010"}, d2 = {"LibraryOverviewScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "modifier", "Landroidx/compose/ui/Modifier;", "onOpenPlaylist", "Lkotlin/Function1;", "", "onOpenLikedSongs", "Lkotlin/Function0;", "LibraryScreen", "LikedSongsQuickAccessRow", "count", "", "onClick", "app_debug"})
public final class LibraryScreenKt {
    
    /**
     * Router: a Spotify-style vertical list of playlists, pushing into
     * [PlaylistDetailScreen] when one is tapped. Navigation state (which playlist, if any, is
     * open) lives in the ViewModel so other tabs (Dashboard) can deep-link into it too.
     */
    @androidx.compose.runtime.Composable()
    public static final void LibraryScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LibraryOverviewScreen(com.vibesync.app.ui.MainViewModel viewModel, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onOpenPlaylist, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenLikedSongs) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LikedSongsQuickAccessRow(int count, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}