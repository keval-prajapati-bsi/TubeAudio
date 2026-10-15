package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.ui.theme.AccentTeal
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Also used as the "Choose theme color" swatches in [CreatePlaylistDialog] -- a playlist with a
 *  user-picked [com.vibesync.app.data.db.PlaylistEntity.themeColorArgb] uses that color directly
 *  instead of the hash-based fallback below. */
val PlaylistThemeColors = listOf(
    Color(0xFFF5A623),
    Color(0xFFE0507E),
    Color(0xFF8B5FBF),
    Color(0xFF4A90D9),
    Color(0xFF3DBFB8),
    Color(0xFF7ED321)
)
private val TileIcons = listOf(Icons.Filled.Headphones, Icons.Filled.MusicNote, Icons.Filled.QueueMusic, Icons.Filled.LibraryMusic)

/** The color a playlist's tile/cover should render with: the user's own pick if they set one,
 *  otherwise a stable hash of its id into [PlaylistThemeColors] so the same playlist always gets
 *  the same color without needing to store one for playlists created before this existed. */
fun playlistTileColor(playlist: PlaylistEntity): Color =
    playlist.themeColorArgb?.let { Color(it) }
        ?: if (playlist.isDefault) NavyPrimary else PlaylistThemeColors[(playlist.id % PlaylistThemeColors.size).toInt()]

/** Same idea as [playlistTileColor] but for the tile's icon -- shared with
 *  [AddToPlaylistDialog]'s redesigned playlist rows so both places render a playlist identically. */
fun playlistTileIcon(playlist: PlaylistEntity): ImageVector =
    if (playlist.isDefault) Icons.Filled.LibraryMusic else TileIcons[(playlist.id % TileIcons.size).toInt()]

/** A single playlist row: colored icon tile, name + song count, a *directly visible* download
 *  button (not buried in a menu -- downloading a whole playlist is common enough to deserve a
 *  one-tap button of its own), and a kebab menu for the rest. Used on both the Library list and
 *  the Dashboard's "Your Playlists" section so they look and behave identically. */
@Composable
fun PlaylistRow(
    playlist: PlaylistEntity,
    trackCount: Int,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    onExport: () -> Unit,
    onRename: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    downloadProgress: Float? = null,
    isFullyDownloaded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val tileColor = playlistTileColor(playlist)
    val tileIcon = playlistTileIcon(playlist)
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(tileColor, tileColor.copy(alpha = 0.75f)))),
            contentAlignment = Alignment.Center
        ) {
            // tileColor can be the light accent (lime, default playlist) or one of the darker
            // PlaylistThemeColors swatches -- a fixed white icon fails contrast on the former, so
            // this picks per-tile rather than assuming every tile is dark enough for white.
            val tileIconTint = if (tileColor.luminance() > 0.5f) com.vibesync.app.ui.theme.OnAccent else SurfaceWhite
            Icon(imageVector = tileIcon, contentDescription = null, tint = tileIconTint)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (trackCount == 1) "1 song" else "$trackCount songs",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        if (isFullyDownloaded) {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.DownloadDone,
                    contentDescription = "Downloaded for offline",
                    tint = AccentTeal
                )
            }
        } else if (downloadProgress != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onDownload),
                contentAlignment = Alignment.Center
            ) {
                // Just kicked off (nothing downloaded yet) reads as "stuck" with a determinate
                // arc sitting at a near-invisible sliver -- an indeterminate spinner communicates
                // "starting" until real byte progress has something to show.
                if (downloadProgress <= 0.01f) {
                    CircularProgressIndicator(modifier = Modifier.size(26.dp), strokeWidth = 2.dp, color = AccentTeal)
                } else {
                    CircularProgressIndicator(
                        progress = downloadProgress,
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.dp,
                        color = AccentTeal
                    )
                }
            }
        } else {
            IconButton(onClick = onDownload, enabled = trackCount > 0) {
                Icon(Icons.Filled.DownloadForOffline, contentDescription = "Download playlist for offline")
            }
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = TextSecondary)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text("Export playlist") },
                    leadingIcon = { Icon(Icons.Filled.IosShare, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onExport()
                    }
                )
                if (onRename != null) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        }
                    )
                }
                if (onDelete != null) {
                    DropdownMenuItem(
                        text = { Text("Delete playlist", color = ErrorRed) },
                        leadingIcon = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = ErrorRed) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
