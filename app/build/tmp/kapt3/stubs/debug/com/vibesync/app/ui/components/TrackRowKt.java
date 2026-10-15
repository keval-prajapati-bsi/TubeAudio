package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aP\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\u00ca\u0001\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\u0010\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00192\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u0019H\u0007\u00a8\u0006\u001e"}, d2 = {"DownloadControlDialog", "", "trackTitle", "", "downloadState", "Lcom/vibesync/app/download/DownloadState;", "onDismiss", "Lkotlin/Function0;", "onPause", "onResume", "onCancel", "TrackRow", "track", "Lcom/vibesync/app/data/db/TrackEntity;", "onPlay", "onAddToPlaylist", "secondaryActionLabel", "onSecondaryAction", "onDownload", "onRemoveDownload", "onPauseDownload", "onResumeDownload", "onCancelDownload", "onAddToQueue", "isCurrentlyPlaying", "", "isInQueue", "modifier", "Landroidx/compose/ui/Modifier;", "compact", "app_debug"})
public final class TrackRowKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void TrackRow(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPlay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddToPlaylist, @org.jetbrains.annotations.NotNull()
    java.lang.String secondaryActionLabel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSecondaryAction, @org.jetbrains.annotations.Nullable()
    com.vibesync.app.download.DownloadState downloadState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRemoveDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPauseDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResumeDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancelDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddToQueue, boolean isCurrentlyPlaying, boolean isInQueue, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean compact) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DownloadControlDialog(java.lang.String trackTitle, com.vibesync.app.download.DownloadState downloadState, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function0<kotlin.Unit> onPause, kotlin.jvm.functions.Function0<kotlin.Unit> onResume, kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
}