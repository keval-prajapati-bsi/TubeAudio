package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.MainViewModel
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/** Custom-styled (not a plain [androidx.compose.material3.AlertDialog]) to match the reference
 *  mockup: a bordered rounded card, a "New playlist" row up top, then each playlist as a tile-
 *  icon + name + song-count row with a squircle checkbox, and a full-width pill CTA at the
 *  bottom -- both the top row and the bottom button open the same "New playlist" flow, which is
 *  now the full [CreatePlaylistDialog] (cover photo/description/theme color), not a bare name
 *  prompt. Rendered as a [ModalBottomSheet] rather than a centered [androidx.compose.ui.window.Dialog]
 *  -- matches the real Figma file's own convention (its "Add to Playlist" screen is explicitly a
 *  bottom sheet, not a centered modal; only short confirm/rename/error prompts are centered there). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistDialog(
    track: TrackEntity,
    playlists: List<PlaylistEntity>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var memberships by remember { mutableStateOf<List<Long>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(track.videoId) {
        memberships = viewModel.playlistIdsForTrack(track.videoId)
    }

    val customPlaylists = playlists.filter { !it.isDefault }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Add to playlist",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                NewPlaylistRow(onClick = { showCreateDialog = true })

                if (customPlaylists.isEmpty()) {
                    Text(
                        text = "No playlists yet -- create one above.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                        items(customPlaylists, key = { it.id }) { playlist ->
                            val count by remember(playlist.id) { viewModel.trackCount(playlist.id) }.collectAsStateWithLifecycle(0)
                            val checked = memberships.contains(playlist.id)
                            PlaylistPickRow(
                                playlist = playlist,
                                trackCount = count,
                                checked = checked,
                                onToggle = {
                                    if (checked) {
                                        viewModel.removeTrackFromPlaylist(playlist.id, track.videoId)
                                        memberships = memberships - playlist.id
                                    } else {
                                        viewModel.addTrackToPlaylist(playlist.id, track.videoId)
                                        memberships = memberships + playlist.id
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = SurfaceWhite)
                    Text(
                        text = " Create new playlist",
                        color = SurfaceWhite,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description, coverImageUri, themeColorArgb ->
                coroutineScope.launch {
                    val newId = viewModel.createPlaylistWithDetailsAndAddTrack(
                        name, description, coverImageUri, themeColorArgb, track.videoId
                    )
                    if (newId != null) memberships = memberships + newId
                }
            }
        )
    }
}

@Composable
private fun NewPlaylistRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = NavyPrimary)
        }
        Text(
            text = "New playlist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun PlaylistPickRow(
    playlist: PlaylistEntity,
    trackCount: Int,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (checked) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(Icons.Filled.Check, contentDescription = "Added", tint = SurfaceWhite, modifier = Modifier.size(14.dp))
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(playlistTileColor(playlist)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = playlistTileIcon(playlist), contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (trackCount == 1) "1 song" else "$trackCount songs",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
