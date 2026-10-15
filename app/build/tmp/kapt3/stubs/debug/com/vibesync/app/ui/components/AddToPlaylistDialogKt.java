package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a4\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a\u0016\u0010\u000b\u001a\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003\u001a.\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003\u00a8\u0006\u0014"}, d2 = {"AddToPlaylistDialog", "", "track", "Lcom/vibesync/app/data/db/TrackEntity;", "playlists", "", "Lcom/vibesync/app/data/db/PlaylistEntity;", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onDismiss", "Lkotlin/Function0;", "NewPlaylistRow", "onClick", "PlaylistPickRow", "playlist", "trackCount", "", "checked", "", "onToggle", "app_debug"})
public final class AddToPlaylistDialogKt {
    
    /**
     * Custom-styled (not a plain [androidx.compose.material3.AlertDialog]) to match the reference
     * mockup: a bordered rounded card, a "New playlist" row up top, then each playlist as a tile-
     * icon + name + song-count row with a squircle checkbox, and a full-width pill CTA at the
     * bottom -- both the top row and the bottom button open the same "New playlist" flow, which is
     * now the full [CreatePlaylistDialog] (cover photo/description/theme color), not a bare name
     * prompt. Rendered as a [ModalBottomSheet] rather than a centered [androidx.compose.ui.window.Dialog]
     * -- matches the real Figma file's own convention (its "Add to Playlist" screen is explicitly a
     * bottom sheet, not a centered modal; only short confirm/rename/error prompts are centered there).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AddToPlaylistDialog(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.PlaylistEntity> playlists, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void NewPlaylistRow(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PlaylistPickRow(com.vibesync.app.data.db.PlaylistEntity playlist, int trackCount, boolean checked, kotlin.jvm.functions.Function0<kotlin.Unit> onToggle) {
    }
}