package com.vibesync.app.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.components.AddToPlaylistDialog
import com.vibesync.app.ui.components.ConfirmDialog
import com.vibesync.app.ui.components.PlaylistCover
import com.vibesync.app.ui.components.ReorderableTrackColumn
import com.vibesync.app.ui.components.SelectableTrackRow
import com.vibesync.app.ui.components.TextPromptDialog
import com.vibesync.app.ui.components.TrackRow
import com.vibesync.app.ui.theme.AccentTeal
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

private enum class SortMode(val label: String) {
    CUSTOM("Custom order"),
    TITLE("Title (A-Z)"),
    RECENT("Recently added")
}

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(playlistId) { viewModel.selectPlaylist(playlistId) }

    val context = LocalContext.current
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val tracks by viewModel.selectedPlaylistTracks.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val ownerName by viewModel.profileName.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    val isFullyDownloaded by remember(playlistId) { viewModel.isPlaylistFullyDownloaded(playlistId) }.collectAsStateWithLifecycle(false)

    val playlist = playlists.firstOrNull { it.id == playlistId } ?: run {
        // Playlist was just deleted (e.g. from here) -- bail back to the list.
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val isCustom = !playlist.isDefault

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var sortMode by remember(playlistId) { mutableStateOf(SortMode.CUSTOM) }
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedVideoIds by remember { mutableStateOf(setOf<String>()) }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedVideoIds = emptySet()
    }

    val totalSeconds = tracks.sumOf { it.durationSeconds }

    Column(modifier = modifier.fillMaxSize()) {
        if (isSelectionMode) {
            SelectionHeader(
                selectedCount = selectedVideoIds.size,
                onCancel = { exitSelectionMode() },
                onExport = {
                    val selected = tracks.filter { selectedVideoIds.contains(it.videoId) }
                    if (selected.isNotEmpty()) {
                        viewModel.exportTracks(selected, "Selected songs") { uri ->
                            context.startActivity(shareIntent(uri))
                        }
                    }
                    exitSelectionMode()
                }
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            PlaylistCover(
                                playlist = playlist,
                                viewModel = viewModel,
                                modifier = Modifier
                                    .size(220.dp)
                                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp)),
                                cornerRadius = 24.dp
                            )
                        }

                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "$ownerName · ${if (tracks.size == 1) "1 song" else "${tracks.size} songs"}" +
                                if (totalSeconds > 0) " · ${formatTotalDuration(totalSeconds)}" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (tracks.isNotEmpty()) MaterialTheme.colorScheme.primary else TextSecondary)
                                    .clickable(enabled = tracks.isNotEmpty()) { viewModel.playSelectedPlaylistFromStart() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = SurfaceWhite, modifier = Modifier.size(28.dp))
                            }
                            RoundIconButton(
                                icon = Icons.Filled.Shuffle,
                                contentDescription = "Shuffle play",
                                enabled = tracks.isNotEmpty(),
                                onClick = { viewModel.shufflePlayPlaylist(playlist.id) }
                            )
                            if (isFullyDownloaded) {
                                RoundIconButton(
                                    icon = Icons.Filled.DownloadDone,
                                    contentDescription = "Downloaded for offline",
                                    enabled = false,
                                    onClick = {},
                                    tint = AccentTeal
                                )
                            } else {
                                RoundIconButton(
                                    icon = Icons.Filled.DownloadForOffline,
                                    contentDescription = "Download for offline",
                                    enabled = tracks.isNotEmpty(),
                                    onClick = { viewModel.downloadPlaylist(tracks) }
                                )
                            }
                            Box {
                                RoundIconButton(
                                    icon = Icons.Filled.MoreVert,
                                    contentDescription = "Playlist options",
                                    enabled = true,
                                    onClick = { showMenu = true }
                                )
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Select songs to export") },
                                        leadingIcon = { Icon(Icons.Filled.Checklist, contentDescription = null) },
                                        enabled = tracks.isNotEmpty(),
                                        onClick = { showMenu = false; isSelectionMode = true }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Export playlist") },
                                        leadingIcon = { Icon(Icons.Filled.IosShare, contentDescription = null) },
                                        enabled = tracks.isNotEmpty(),
                                        onClick = {
                                            showMenu = false
                                            viewModel.exportPlaylist(playlist.id, playlist.name) { uri ->
                                                context.startActivity(shareIntent(uri))
                                            }
                                        }
                                    )
                                    if (isCustom) {
                                        DropdownMenuItem(
                                            text = { Text("Rename") },
                                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                            onClick = { showMenu = false; showRenameDialog = true }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete playlist", color = ErrorRed) },
                                            leadingIcon = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = ErrorRed) },
                                            onClick = { showMenu = false; showDeleteConfirm = true }
                                        )
                                    }
                                }
                            }
                        }

                        if (isCustom && tracks.isNotEmpty()) {
                            Box(modifier = Modifier.padding(top = 18.dp)) {
                                Row(
                                    modifier = Modifier.clickable { showSortMenu = true },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sortMode.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(Icons.Filled.SwapVert, contentDescription = "Change sort order", tint = TextSecondary, modifier = Modifier.padding(start = 6.dp).size(18.dp))
                                }
                                DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                                    SortMode.values().forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(mode.label) },
                                            onClick = { showSortMenu = false; sortMode = mode }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (tracks.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isCustom) {
                                    "This playlist is empty. Use \"Add to playlist\" on any track to add one."
                                } else {
                                    "Links you play will show up here."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (isCustom && sortMode != SortMode.CUSTOM) {
                    val sorted = if (sortMode == SortMode.TITLE) {
                        tracks.sortedBy { it.title.lowercase() }
                    } else {
                        tracks.sortedByDescending { it.addedAtEpochMillis }
                    }
                    items(sorted, key = { it.videoId }) { track ->
                        TrackRow(
                            track = track,
                            onPlay = { viewModel.playFromSelectedPlaylist(track) },
                            onAddToPlaylist = { addToPlaylistTrack = track },
                            secondaryActionLabel = "Remove from playlist",
                            onSecondaryAction = { viewModel.removeTrackFromPlaylist(playlist.id, track.videoId) },
                            downloadState = downloadStates[track.videoId],
                            onDownload = { viewModel.downloadTrack(track) },
                            onRemoveDownload = { viewModel.removeDownload(track) },
                            onPauseDownload = { viewModel.pauseDownload(track.videoId) },
                            onResumeDownload = { viewModel.resumeDownload(track) },
                            onCancelDownload = { viewModel.cancelDownload(track.videoId) },
                            onAddToQueue = { viewModel.addTrackToQueue(track) },
                            isCurrentlyPlaying = playbackState.isPlaying && playbackState.videoId == track.videoId,
                            isInQueue = queuedVideoIds.contains(track.videoId),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else if (!isCustom) {
                    items(tracks, key = { it.videoId }) { track ->
                        TrackRow(
                            track = track,
                            onPlay = { viewModel.playFromSelectedPlaylist(track) },
                            onAddToPlaylist = { addToPlaylistTrack = track },
                            secondaryActionLabel = "Delete",
                            onSecondaryAction = { viewModel.deleteTrack(track.videoId) },
                            downloadState = downloadStates[track.videoId],
                            onDownload = { viewModel.downloadTrack(track) },
                            onRemoveDownload = { viewModel.removeDownload(track) },
                            onPauseDownload = { viewModel.pauseDownload(track.videoId) },
                            onResumeDownload = { viewModel.resumeDownload(track) },
                            onCancelDownload = { viewModel.cancelDownload(track.videoId) },
                            onAddToQueue = { viewModel.addTrackToQueue(track) },
                            isCurrentlyPlaying = playbackState.isPlaying && playbackState.videoId == track.videoId,
                            isInQueue = queuedVideoIds.contains(track.videoId),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    item {
                        ReorderableTrackColumn(
                            tracks = tracks,
                            downloadStates = downloadStates,
                            onOrderChanged = { newOrder -> viewModel.reorderPlaylist(playlist.id, newOrder.map { it.videoId }) },
                            onPlay = { viewModel.playFromSelectedPlaylist(it) },
                            onAddToPlaylist = { addToPlaylistTrack = it },
                            onRemove = { viewModel.removeTrackFromPlaylist(playlist.id, it.videoId) },
                            onDownload = { viewModel.downloadTrack(it) },
                            onRemoveDownload = { viewModel.removeDownload(it) },
                            onPauseDownload = { viewModel.pauseDownload(it) },
                            onResumeDownload = { viewModel.resumeDownload(it) },
                            onCancelDownload = { viewModel.cancelDownload(it) },
                            onAddToQueue = { viewModel.addTrackToQueue(it) },
                            currentlyPlayingVideoId = if (playbackState.isPlaying) playbackState.videoId else null,
                            queuedVideoIds = queuedVideoIds,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
        if (isSelectionMode) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(tracks, key = { it.videoId }) { track ->
                    SelectableTrackRow(
                        track = track,
                        isSelected = selectedVideoIds.contains(track.videoId),
                        onToggle = {
                            selectedVideoIds = if (selectedVideoIds.contains(track.videoId)) {
                                selectedVideoIds - track.videoId
                            } else {
                                selectedVideoIds + track.videoId
                            }
                        }
                    )
                }
            }
        }
    }

    if (showRenameDialog) {
        TextPromptDialog(
            title = "Rename playlist",
            label = "Playlist name",
            confirmLabel = "Save",
            initialValue = playlist.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { name -> viewModel.renamePlaylist(playlist.id, name) }
        )
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete \"${playlist.name}\"?",
            message = "This only removes the playlist. The songs stay in your library.",
            onConfirm = {
                viewModel.deletePlaylist(playlist.id)
                onBack()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    addToPlaylistTrack?.let { track ->
        AddToPlaylistDialog(
            track = track,
            playlists = playlists,
            viewModel = viewModel,
            onDismiss = { addToPlaylistTrack = null }
        )
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color? = null
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint ?: if (enabled) MaterialTheme.colorScheme.onSurface else TextSecondary
        )
    }
}

@Composable
private fun SelectionHeader(selectedCount: Int, onCancel: () -> Unit, onExport: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCancel) {
            Icon(Icons.Filled.Close, contentDescription = "Cancel selection")
        }
        Text(
            text = if (selectedCount == 0) "Select songs" else "$selectedCount selected",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onExport, enabled = selectedCount > 0) {
            Icon(Icons.Filled.IosShare, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
            Text("Export")
        }
    }
}

private fun shareIntent(uri: android.net.Uri): Intent =
    Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
