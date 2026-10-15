package com.vibesync.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.components.AddToPlaylistDialog
import com.vibesync.app.ui.components.TrackRow
import com.vibesync.app.ui.theme.TextSecondary

@Composable
fun LikedSongsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val tracks by viewModel.likedTracks.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Liked Songs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Songs you like will show up here.\nTap the heart on any search or trending result.",
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
                        onPlay = { viewModel.playLikedTrack(track) },
                        onAddToPlaylist = { addToPlaylistTrack = track },
                        secondaryActionLabel = "Unlike",
                        onSecondaryAction = { viewModel.toggleLike(track.videoId) },
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
