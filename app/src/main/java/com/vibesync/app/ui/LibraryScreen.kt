package com.vibesync.app.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.ui.components.ConfirmDialog
import com.vibesync.app.ui.components.CreatePlaylistDialog
import com.vibesync.app.ui.components.PlaylistRow
import com.vibesync.app.ui.components.TextPromptDialog
import com.vibesync.app.ui.components.TrackRow
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Router: a Spotify-style vertical list of playlists, pushing into
 *  [PlaylistDetailScreen] when one is tapped. Navigation state (which playlist, if any, is
 *  open) lives in the ViewModel so other tabs (Dashboard) can deep-link into it too. */
@Composable
fun LibraryScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val playlistId by viewModel.openPlaylistId.collectAsStateWithLifecycle()
    val showLikedSongs by viewModel.showLikedSongs.collectAsStateWithLifecycle()

    BackHandler(enabled = playlistId != null || showLikedSongs) {
        if (showLikedSongs) viewModel.closeLikedSongs() else viewModel.openPlaylist(null)
    }

    when {
        showLikedSongs -> LikedSongsScreen(
            viewModel = viewModel,
            modifier = modifier,
            onBack = { viewModel.closeLikedSongs() }
        )
        playlistId != null -> PlaylistDetailScreen(
            playlistId = playlistId!!,
            viewModel = viewModel,
            modifier = modifier,
            onBack = { viewModel.openPlaylist(null) }
        )
        else -> LibraryOverviewScreen(
            viewModel = viewModel,
            modifier = modifier,
            onOpenPlaylist = { viewModel.openPlaylist(it) },
            onOpenLikedSongs = { viewModel.openLikedSongs() }
        )
    }
}

@Composable
private fun LibraryOverviewScreen(
    viewModel: MainViewModel,
    modifier: Modifier,
    onOpenPlaylist: (Long) -> Unit,
    onOpenLikedSongs: () -> Unit
) {
    val context = LocalContext.current
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val likedTracks by viewModel.likedTracks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<PlaylistEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<PlaylistEntity?>(null) }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importPlaylist(uri)
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive) viewModel.onSearchQueryChanged("")
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )
                IconButton(onClick = { isSearchActive = !isSearchActive }) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = if (isSearchActive) "Close search" else "Search songs",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { importLauncher.launch(arrayOf("*/*")) }) {
                    Icon(
                        Icons.Filled.FileUpload,
                        contentDescription = "Import playlist",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    placeholder = { Text("Search your songs") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = NavyPrimary
                    )
                )

                if (searchQuery.isBlank()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Find a song by title.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                } else if (searchResults.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No songs match \"$searchQuery\".",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                        items(searchResults, key = { it.videoId }) { track ->
                            TrackRow(
                                track = track,
                                onPlay = { viewModel.playFromDefault(track) },
                                onAddToPlaylist = { },
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
                                isInQueue = queuedVideoIds.contains(track.videoId)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    item {
                        LikedSongsQuickAccessRow(count = likedTracks.size, onClick = onOpenLikedSongs)
                    }
                    items(playlists, key = { it.id }) { playlist ->
                        val count by remember(playlist.id) { viewModel.trackCount(playlist.id) }.collectAsStateWithLifecycle(0)
                        val downloadProgress by remember(playlist.id) {
                            viewModel.playlistDownloadProgress(playlist.id)
                        }.collectAsStateWithLifecycle(null)
                        val isFullyDownloaded by remember(playlist.id) {
                            viewModel.isPlaylistFullyDownloaded(playlist.id)
                        }.collectAsStateWithLifecycle(false)
                        PlaylistRow(
                            playlist = playlist,
                            trackCount = count,
                            downloadProgress = downloadProgress,
                            isFullyDownloaded = isFullyDownloaded,
                            onClick = { onOpenPlaylist(playlist.id) },
                            onDownload = { viewModel.downloadPlaylistById(playlist.id) },
                            onExport = {
                                viewModel.exportPlaylist(playlist.id, playlist.name) { uri ->
                                    context.startActivity(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                    )
                                }
                            },
                            onRename = if (!playlist.isDefault) ({ renameTarget = playlist }) else null,
                            onDelete = if (!playlist.isDefault) ({ deleteTarget = playlist }) else null
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (!isSearchActive) {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = SurfaceWhite
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New playlist")
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description, coverImageUri, themeColorArgb ->
                viewModel.createPlaylist(name, description, coverImageUri, themeColorArgb)
            }
        )
    }

    renameTarget?.let { playlist ->
        TextPromptDialog(
            title = "Rename playlist",
            label = "Playlist name",
            confirmLabel = "Save",
            initialValue = playlist.name,
            onDismiss = { renameTarget = null },
            onConfirm = { name -> viewModel.renamePlaylist(playlist.id, name) }
        )
    }

    deleteTarget?.let { playlist ->
        ConfirmDialog(
            title = "Delete \"${playlist.name}\"?",
            message = "This only removes the playlist. The songs stay in your library.",
            onConfirm = { viewModel.deletePlaylist(playlist.id) },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun LikedSongsQuickAccessRow(count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ErrorRed),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = null, tint = SurfaceWhite)
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = "Liked Songs",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (count == 1) "1 song" else "$count songs",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
