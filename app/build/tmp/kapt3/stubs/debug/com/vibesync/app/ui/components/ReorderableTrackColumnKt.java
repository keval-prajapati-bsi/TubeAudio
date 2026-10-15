package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u00a0\u0002\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00062\u0018\u0010\t\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\n2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u00a8\u0006\u0019"}, d2 = {"ReorderableTrackColumn", "", "tracks", "", "Lcom/vibesync/app/data/db/TrackEntity;", "downloadStates", "", "", "Lcom/vibesync/app/download/DownloadState;", "onOrderChanged", "Lkotlin/Function1;", "onPlay", "onAddToPlaylist", "onRemove", "onDownload", "onRemoveDownload", "onPauseDownload", "onResumeDownload", "onCancelDownload", "onAddToQueue", "currentlyPlayingVideoId", "queuedVideoIds", "", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class ReorderableTrackColumnKt {
    
    /**
     * A track list with a drag handle for manual reordering -- "song priority" for custom playlists.
     * Thin wrapper around [DragReorderColumn] (shared with the Queue screen's "Up Next" reordering)
     * that supplies [TrackRow] as the row content. Rows render in [TrackRow]'s `compact` mode both to
     * fit more on screen while reordering and to keep row-height measurement small and consistent.
     */
    @androidx.compose.runtime.Composable()
    public static final void ReorderableTrackColumn(@org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> tracks, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends com.vibesync.app.download.DownloadState> downloadStates, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.vibesync.app.data.db.TrackEntity>, kotlin.Unit> onOrderChanged, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onPlay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onAddToPlaylist, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onRemove, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onRemoveDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPauseDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onResumeDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCancelDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.data.db.TrackEntity, kotlin.Unit> onAddToQueue, @org.jetbrains.annotations.Nullable()
    java.lang.String currentlyPlayingVideoId, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> queuedVideoIds, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}