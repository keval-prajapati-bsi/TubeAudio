package com.vibesync.app.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.components.AddToPlaylistDialog
import com.vibesync.app.ui.components.PlaylistRow
import com.vibesync.app.ui.components.TrackRow
import com.vibesync.app.ui.theme.AccentTeal
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceContainer
import com.vibesync.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun DownloadsScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var showDownloadedPlaylists by remember { mutableStateOf(false) }
    var showDownloadedSongs by remember { mutableStateOf(false) }

    if (showDownloadedPlaylists) {
        DownloadedPlaylistsScreen(viewModel = viewModel, modifier = modifier, onBack = { showDownloadedPlaylists = false })
        return
    }

    if (showDownloadedSongs) {
        DownloadedSongsScreen(viewModel = viewModel, modifier = modifier, onBack = { showDownloadedSongs = false })
        return
    }

    val tracks by viewModel.downloadsScreenTracks.collectAsStateWithLifecycle()
    val downloadedCount by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val downloadedPlaylistCount by viewModel.fullyDownloadedPlaylistCount.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.refreshStorageInfo() }

    val visibleTracks = if (isSearchActive && searchQuery.isNotBlank()) {
        tracks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    } else {
        tracks
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Downloads",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )
            IconButton(onClick = {
                isSearchActive = !isSearchActive
                if (!isSearchActive) searchQuery = ""
            }) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = if (isSearchActive) "Close search" else "Search downloads"
                )
            }
        }

        if (isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                placeholder = { Text("Search downloads") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = NavyPrimary
                )
            )
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            if (!isSearchActive) {
                item { StorageSummaryCard(storageInfo) }
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        CategoryCard(
                            icon = Icons.Filled.MusicNote,
                            label = "Downloaded songs",
                            count = downloadedCount.size,
                            onClick = { showDownloadedSongs = true },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        CategoryCard(
                            icon = Icons.Filled.LibraryMusic,
                            label = "Downloaded playlists",
                            count = downloadedPlaylistCount,
                            onClick = { showDownloadedPlaylists = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Text(
                        text = "Recently downloaded",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
            }

            if (visibleTracks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (tracks.isEmpty()) {
                                "Downloaded songs will show up here.\nTap the download icon on any song or playlist to make it available offline."
                            } else {
                                "No downloads match \"$searchQuery\"."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(visibleTracks, key = { it.videoId }) { track ->
                    TrackRow(
                        track = track,
                        onPlay = { viewModel.playDownloadedTrack(track) },
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
                        isInQueue = queuedVideoIds.contains(track.videoId)
                    )
                }
            }
        }
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
private fun StorageSummaryCard(storageInfo: com.vibesync.app.data.StorageInfo?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = storageInfo?.usedFraction ?: 0f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 5.dp,
                color = NavyPrimary,
                // A clearly neutral grey for the *remaining* space -- CardBorder is theme-tinted
                // and too close to the card's own background to read as "space left."
                trackColor = TextSecondary.copy(alpha = 0.25f)
            )
            Text(
                text = "${(((storageInfo?.usedFraction ?: 0f) * 100).toInt())}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = "Storage used",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (storageInfo != null) {
                    "${formatBytes(storageInfo.usedByAppBytes)} of ${formatBytes(storageInfo.deviceTotalBytes)}"
                } else {
                    "Calculating..."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DownloadedSongsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val tracks by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Downloaded songs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No songs are downloaded yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(tracks, key = { it.videoId }) { track ->
                    TrackRow(
                        track = track,
                        onPlay = { viewModel.playDownloadedTrack(track) },
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
                        isInQueue = queuedVideoIds.contains(track.videoId)
                    )
                }
            }
        }
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
private fun DownloadedPlaylistsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val playlists by viewModel.fullyDownloadedPlaylists.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Downloaded playlists",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (playlists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No playlists are fully downloaded yet.\nDownload every song in a playlist and it'll show up here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(playlists, key = { it.id }) { playlist ->
                    val count by remember(playlist.id) { viewModel.trackCount(playlist.id) }.collectAsStateWithLifecycle(0)
                    PlaylistRow(
                        playlist = playlist,
                        trackCount = count,
                        isFullyDownloaded = true,
                        onClick = {
                            viewModel.openPlaylist(playlist.id)
                            viewModel.selectTab(AppTab.LIBRARY)
                        },
                        onDownload = { viewModel.downloadPlaylistById(playlist.id) },
                        onExport = {
                            viewModel.exportPlaylist(playlist.id, playlist.name) { uri ->
                                context.startActivity(
                                    android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                )
                            }
                        },
                        onRename = null,
                        onDelete = null
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentTeal),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = com.vibesync.app.ui.theme.SurfaceWhite, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = if (count == 1) "1" else "$count",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1)
        }
    }
}
