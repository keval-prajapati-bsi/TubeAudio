package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.ui.MainViewModel
import com.vibesync.app.ui.theme.SurfaceWhite

/** Playlist "cover art", in priority order: a user-picked cover photo
 *  ([PlaylistEntity.coverImageUri]), then the most-recently-added track's thumbnail, then a
 *  tinted tile in the playlist's [playlistTileColor] so it always reads as belonging to that
 *  playlist even with nothing else to show. */
@Composable
fun PlaylistCover(
    playlist: PlaylistEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp
) {
    val trackCover by remember(playlist.id) { viewModel.coverThumbnail(playlist.id) }.collectAsStateWithLifecycle(null)
    val cover = playlist.coverImageUri ?: trackCover
    val tileColor = playlistTileColor(playlist)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(if (cover != null) SurfaceWhite else tileColor),
        contentAlignment = Alignment.Center
    ) {
        if (cover != null) {
            AsyncImage(
                model = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = if (playlist.isDefault) Icons.Filled.LibraryMusic else Icons.Filled.QueueMusic,
                contentDescription = null,
                tint = SurfaceWhite
            )
        }
    }
}
