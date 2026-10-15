package com.vibesync.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.download.DownloadState
import com.vibesync.app.ui.theme.TextSecondary

/**
 * A track list with a drag handle for manual reordering -- "song priority" for custom playlists.
 * Thin wrapper around [DragReorderColumn] (shared with the Queue screen's "Up Next" reordering)
 * that supplies [TrackRow] as the row content. Rows render in [TrackRow]'s `compact` mode both to
 * fit more on screen while reordering and to keep row-height measurement small and consistent.
 */
@Composable
fun ReorderableTrackColumn(
    tracks: List<TrackEntity>,
    downloadStates: Map<String, DownloadState>,
    onOrderChanged: (List<TrackEntity>) -> Unit,
    onPlay: (TrackEntity) -> Unit,
    onAddToPlaylist: (TrackEntity) -> Unit,
    onRemove: (TrackEntity) -> Unit,
    onDownload: (TrackEntity) -> Unit,
    onRemoveDownload: (TrackEntity) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (TrackEntity) -> Unit,
    onCancelDownload: (String) -> Unit,
    onAddToQueue: (TrackEntity) -> Unit = {},
    currentlyPlayingVideoId: String? = null,
    queuedVideoIds: Set<String> = emptySet(),
    modifier: Modifier = Modifier
) {
    DragReorderColumn(
        items = tracks,
        key = { it.videoId },
        onOrderChanged = onOrderChanged,
        modifier = modifier
    ) { track, _, dragModifier ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.DragHandle,
                contentDescription = "Drag to reorder",
                tint = TextSecondary,
                modifier = Modifier
                    .padding(8.dp)
                    .then(dragModifier)
            )
            TrackRow(
                track = track,
                onPlay = { onPlay(track) },
                onAddToPlaylist = { onAddToPlaylist(track) },
                secondaryActionLabel = "Remove from playlist",
                onSecondaryAction = { onRemove(track) },
                downloadState = downloadStates[track.videoId],
                onDownload = { onDownload(track) },
                onRemoveDownload = { onRemoveDownload(track) },
                onPauseDownload = { onPauseDownload(track.videoId) },
                onResumeDownload = { onResumeDownload(track) },
                onCancelDownload = { onCancelDownload(track.videoId) },
                onAddToQueue = { onAddToQueue(track) },
                isCurrentlyPlaying = currentlyPlayingVideoId == track.videoId,
                isInQueue = queuedVideoIds.contains(track.videoId),
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
