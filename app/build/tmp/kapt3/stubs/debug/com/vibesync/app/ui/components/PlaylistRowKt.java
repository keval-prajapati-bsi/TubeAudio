package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u0087\u0001\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\b0\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u000e2\u000e\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000e2\u000e\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000e2\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u00a2\u0006\u0002\u0010\u0019\u001a\u0013\u0010\u001a\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u001b\u001a\u000e\u0010\u001c\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"PlaylistThemeColors", "", "Landroidx/compose/ui/graphics/Color;", "getPlaylistThemeColors", "()Ljava/util/List;", "TileIcons", "Landroidx/compose/ui/graphics/vector/ImageVector;", "PlaylistRow", "", "playlist", "Lcom/vibesync/app/data/db/PlaylistEntity;", "trackCount", "", "onClick", "Lkotlin/Function0;", "onDownload", "onExport", "onRename", "onDelete", "downloadProgress", "", "isFullyDownloaded", "", "modifier", "Landroidx/compose/ui/Modifier;", "(Lcom/vibesync/app/data/db/PlaylistEntity;ILkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Ljava/lang/Float;ZLandroidx/compose/ui/Modifier;)V", "playlistTileColor", "(Lcom/vibesync/app/data/db/PlaylistEntity;)J", "playlistTileIcon", "app_debug"})
public final class PlaylistRowKt {
    
    /**
     * Also used as the "Choose theme color" swatches in [CreatePlaylistDialog] -- a playlist with a
     * user-picked [com.vibesync.app.data.db.PlaylistEntity.themeColorArgb] uses that color directly
     * instead of the hash-based fallback below.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<androidx.compose.ui.graphics.Color> PlaylistThemeColors = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<androidx.compose.ui.graphics.vector.ImageVector> TileIcons = null;
    
    /**
     * Also used as the "Choose theme color" swatches in [CreatePlaylistDialog] -- a playlist with a
     * user-picked [com.vibesync.app.data.db.PlaylistEntity.themeColorArgb] uses that color directly
     * instead of the hash-based fallback below.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<androidx.compose.ui.graphics.Color> getPlaylistThemeColors() {
        return null;
    }
    
    /**
     * The color a playlist's tile/cover should render with: the user's own pick if they set one,
     * otherwise a stable hash of its id into [PlaylistThemeColors] so the same playlist always gets
     * the same color without needing to store one for playlists created before this existed.
     */
    public static final long playlistTileColor(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaylistEntity playlist) {
        return 0L;
    }
    
    /**
     * Same idea as [playlistTileColor] but for the tile's icon -- shared with
     * [AddToPlaylistDialog]'s redesigned playlist rows so both places render a playlist identically.
     */
    @org.jetbrains.annotations.NotNull()
    public static final androidx.compose.ui.graphics.vector.ImageVector playlistTileIcon(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaylistEntity playlist) {
        return null;
    }
    
    /**
     * A single playlist row: colored icon tile, name + song count, a *directly visible* download
     * button (not buried in a menu -- downloading a whole playlist is common enough to deserve a
     * one-tap button of its own), and a kebab menu for the rest. Used on both the Library list and
     * the Dashboard's "Your Playlists" section so they look and behave identically.
     */
    @androidx.compose.runtime.Composable()
    public static final void PlaylistRow(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaylistEntity playlist, int trackCount, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExport, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRename, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, @org.jetbrains.annotations.Nullable()
    java.lang.Float downloadProgress, boolean isFullyDownloaded, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}