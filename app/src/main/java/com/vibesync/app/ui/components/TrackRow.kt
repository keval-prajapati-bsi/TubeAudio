package com.vibesync.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.widget.Toast
import coil.compose.AsyncImage
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.download.DownloadState
import com.vibesync.app.ui.formatDuration
import com.vibesync.app.ui.theme.AccentTeal
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRow(
    track: TrackEntity,
    onPlay: () -> Unit,
    onAddToPlaylist: () -> Unit,
    secondaryActionLabel: String,
    onSecondaryAction: () -> Unit,
    downloadState: DownloadState?,
    onDownload: () -> Unit,
    onRemoveDownload: () -> Unit,
    onPauseDownload: () -> Unit,
    onResumeDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onAddToQueue: () -> Unit = {},
    isCurrentlyPlaying: Boolean = false,
    isInQueue: Boolean = false,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val thumbnailSize = if (compact) 40.dp else 52.dp
    val verticalPadding = if (compact) 6.dp else 10.dp
    val cornerRadius = if (compact) 10.dp else 12.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (isCurrentlyPlaying) Modifier.background(NavyPrimary.copy(alpha = 0.08f)) else Modifier
            )
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onPlay()
            }
            .padding(vertical = verticalPadding, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(thumbnailSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(BackgroundWhite)
                .then(
                    if (isCurrentlyPlaying) {
                        Modifier.border(BorderStroke(1.5.dp, NavyPrimary), RoundedCornerShape(cornerRadius))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (track.thumbnailUrl != null) {
                AsyncImage(
                    model = track.thumbnailUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(thumbnailSize)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(cornerRadius))
                )
            } else {
                Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
            if (isCurrentlyPlaying) {
                Box(
                    modifier = Modifier
                        .size(thumbnailSize)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(NavyPrimary.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    SoundBarsIndicator(barColor = com.vibesync.app.ui.theme.OnAccent)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isCurrentlyPlaying) NavyPrimary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (track.isDownloaded) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Downloaded",
                        tint = AccentTeal,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(end = 4.dp)
                    )
                }
                Text(
                    text = formatDuration(track.durationSeconds * 1000),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        if (downloadState != null) {
            val fraction = when (downloadState) {
                is DownloadState.Downloading -> downloadState.fraction
                is DownloadState.Paused -> downloadState.fraction
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clickable { showDownloadDialog = true },
                contentAlignment = Alignment.Center
            ) {
                // Same "just started" spinner treatment as PlaylistRow's ring -- a determinate
                // arc sitting at 0% reads as stuck, not starting.
                if (fraction <= 0.01f && downloadState is DownloadState.Downloading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize(), strokeWidth = 2.dp, color = AccentTeal)
                } else {
                    CircularProgressIndicator(
                        progress = fraction,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 2.dp,
                        color = AccentTeal
                    )
                }
                if (downloadState is DownloadState.Paused) {
                    Icon(
                        imageVector = Icons.Filled.Pause,
                        contentDescription = "Paused",
                        tint = AccentTeal,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options", tint = TextSecondary)
            }
        }
    }

    // Track options as a bottom sheet, not a dropdown -- matches the real Figma file's own track
    // options screen exactly (title/artist header, then Add to Playlist/Add to Queue/Download/
    // Share/etc as full-width rows). "Go to Artist"/"Report" from that reference aren't included:
    // this app has no artist-page or content-moderation feature behind them, so they'd be dead
    // taps -- same "don't fake a feature that isn't real" discipline as everywhere else in this app.
    if (menuExpanded) {
        ModalBottomSheet(onDismissRequest = { menuExpanded = false }, sheetState = rememberModalBottomSheetState()) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                DropdownMenuItem(
                    text = { Text("Add to playlist") },
                    leadingIcon = { Icon(Icons.Filled.PlaylistAdd, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onAddToPlaylist()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (isInQueue) "Already in queue" else "Add to queue") },
                    leadingIcon = { Icon(Icons.Filled.QueueMusic, contentDescription = null) },
                    enabled = !isInQueue,
                    onClick = {
                        menuExpanded = false
                        onAddToQueue()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Copy link") },
                    leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        clipboardManager.setText(AnnotatedString(track.url))
                        Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                    }
                )
                if (downloadState == null) {
                    DropdownMenuItem(
                        text = { Text(if (track.isDownloaded) "Remove download" else "Download for offline") },
                        leadingIcon = { Icon(Icons.Filled.DownloadForOffline, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            if (track.isDownloaded) onRemoveDownload() else onDownload()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(secondaryActionLabel, color = ErrorRed) },
                    onClick = {
                        menuExpanded = false
                        showConfirmDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showDownloadDialog && downloadState != null) {
        DownloadControlDialog(
            trackTitle = track.title,
            downloadState = downloadState,
            onDismiss = { showDownloadDialog = false },
            onPause = onPauseDownload,
            onResume = onResumeDownload,
            onCancel = onCancelDownload
        )
    }

    if (showConfirmDialog) {
        ConfirmDialog(
            title = "$secondaryActionLabel?",
            message = "\"${track.title}\" -- this can't be undone.",
            confirmLabel = secondaryActionLabel,
            onConfirm = onSecondaryAction,
            onDismiss = { showConfirmDialog = false }
        )
    }
}

@Composable
private fun DownloadControlDialog(
    trackTitle: String,
    downloadState: DownloadState,
    onDismiss: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    val fraction = when (downloadState) {
        is DownloadState.Downloading -> downloadState.fraction
        is DownloadState.Paused -> downloadState.fraction
    }
    val isPaused = downloadState is DownloadState.Paused

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(trackTitle, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        text = {
            Column {
                Text(
                    text = if (isPaused) "Download paused" else "Downloading for offline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(progress = fraction, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(fraction * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (isPaused) onResume() else onPause()
                onDismiss()
            }) {
                Text(if (isPaused) "Resume" else "Pause")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
                onDismiss()
            }) { Text("Stop downloading") }
        }
    )
}
