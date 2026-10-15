package com.vibesync.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.components.AddToPlaylistDialog
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.HeroGradient
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NowPlayingScreen(viewModel: MainViewModel, onClose: () -> Unit) {
    val state by viewModel.playbackState.collectAsStateWithLifecycle()
    val likedVideoIds by viewModel.likedVideoIds.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val showQueue by viewModel.showQueue.collectAsStateWithLifecycle()

    var showMoreMenu by remember { mutableStateOf(false) }
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (showQueue) {
        QueueScreen(viewModel = viewModel, onBack = { viewModel.closeQueue() })
        return
    }

    val isLiked = state.videoId != null && likedVideoIds.contains(state.videoId)
    val haptics = LocalHapticFeedback.current
    fun tap() = haptics.performHapticFeedback(HapticFeedbackType.LongPress)

    // Real per-track dynamic background: extracts the current art's own dominant/vibrant color via
    // the Android Palette API and uses it as a gradient tint, replacing the earlier static-blur
    // placeholder -- this is genuinely "reads the photo," not an approximation of it. `allowHardware
    // (false)` on the request is required: Coil's default hardware bitmaps can't have their pixels
    // read by Palette (it throws), so this specific request must opt back into a software bitmap.
    // Runs on Dispatchers.IO since both the image decode and Palette.generate() are blocking calls.
    var ambientColor by remember { mutableStateOf<Color?>(null) }
    LaunchedEffect(state.thumbnailUrl) {
        ambientColor = null
        val url = state.thumbnailUrl ?: return@LaunchedEffect
        ambientColor = withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
                val bitmap = (context.imageLoader.execute(request).drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                bitmap?.let { bmp ->
                    val palette = Palette.Builder(bmp).generate()
                    val swatch = palette.vibrantSwatch ?: palette.dominantSwatch ?: palette.mutedSwatch
                    swatch?.let { Color(it.rgb) }
                }
            } catch (e: Exception) {
                android.util.Log.w("NowPlayingScreen", "Palette extraction failed for $url", e)
                null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val pageBackground = MaterialTheme.colorScheme.background
        val tint = ambientColor ?: pageBackground
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tint.copy(alpha = 0.55f),
                            tint.copy(alpha = 0.22f),
                            pageBackground
                        )
                    )
                )
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PLAYING FROM",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = state.queueLabel.ifBlank { "Now Playing" },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { viewModel.openQueue() }) {
                Icon(Icons.Filled.QueueMusic, contentDescription = "Queue", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerAlbumArt), ambientColor = HeroGradient[0], spotColor = HeroGradient[0])
                    .clip(RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerAlbumArt))
                    .background(BackgroundWhite),
                contentAlignment = Alignment.Center
            ) {
                if (state.thumbnailUrl != null) {
                    AsyncImage(
                        model = state.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.title ?: "Nothing playing",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { tap(); viewModel.toggleLikeCurrentTrack() }, enabled = state.videoId != null) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) ErrorRed else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = state.positionMs.toFloat().coerceAtMost(state.durationMs.toFloat().coerceAtLeast(0f)),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..(state.durationMs.toFloat().coerceAtLeast(1f)),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = BackgroundWhite
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatDuration(state.positionMs), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(formatDuration(state.durationMs), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        if (state.chapters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            ChapterRow(
                chapters = state.chapters,
                positionMs = state.positionMs,
                onJumpTo = { viewModel.seekTo(it) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { tap(); viewModel.toggleShuffle() }) {
                Icon(
                    Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (state.isShuffleEnabled) MaterialTheme.colorScheme.primary else TextSecondary
                )
            }
            IconButton(onClick = { tap(); viewModel.skipToPrevious() }, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp))
            }
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(50), ambientColor = MaterialTheme.colorScheme.primary)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.linearGradient(HeroGradient))
                    .clickable { tap(); viewModel.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = { tap(); viewModel.skipToNext() }, enabled = state.hasNext, modifier = Modifier.size(44.dp)) {
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(36.dp),
                    tint = if (state.hasNext) MaterialTheme.colorScheme.onSurface else TextSecondary
                )
            }
            IconButton(onClick = { tap(); viewModel.toggleRepeat() }) {
                Icon(
                    Icons.Filled.Repeat,
                    contentDescription = "Repeat",
                    tint = if (state.isRepeatEnabled) MaterialTheme.colorScheme.primary else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BottomIconAction(
                icon = Icons.Filled.Lyrics,
                label = "Lyrics",
                onClick = { viewModel.openLyrics() }
            )
            BottomIconAction(icon = Icons.Filled.QueueMusic, label = "Queue", onClick = { viewModel.openQueue() })
            BottomIconAction(
                icon = Icons.Filled.Equalizer,
                label = "Equalizer",
                onClick = { viewModel.openEqualizer() }
            )
            Box {
                BottomIconAction(icon = Icons.Filled.MoreVert, label = "More", onClick = { showMoreMenu = true })
                DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Add to playlist") },
                        leadingIcon = { Icon(Icons.Filled.PlaylistAdd, contentDescription = null) },
                        enabled = state.videoId != null,
                        onClick = {
                            showMoreMenu = false
                            coroutineScope.launch {
                                addToPlaylistTrack = viewModel.ensureCurrentTrackSaved()
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Copy link") },
                        leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
                        enabled = state.url != null,
                        onClick = {
                            showMoreMenu = false
                            state.url?.let {
                                clipboardManager.setText(AnnotatedString(it))
                                Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
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

/** YouTube's own chapter markers ("sections between videos" you can jump between) -- a horizontal
 *  row of tappable chips rather than ticks on the slider itself, since Compose's `Slider` has no
 *  built-in way to draw extra marks on its track without a fully custom implementation; this reads
 *  just as clearly and is simpler to get right. The chapter containing the current position is
 *  highlighted so it's obvious which section is currently playing. */
@Composable
private fun ChapterRow(chapters: List<com.vibesync.app.extractor.Chapter>, positionMs: Long, onJumpTo: (Long) -> Unit) {
    val positionSeconds = (positionMs / 1000L).toInt()
    val activeIndex = chapters.indexOfLast { it.startTimeSeconds <= positionSeconds }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(chapters.size) { index ->
            val chapter = chapters[index]
            val isActive = index == activeIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) MaterialTheme.colorScheme.primary else BackgroundWhite)
                    .clickable { onJumpTo(chapter.startTimeSeconds * 1000L) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isActive) SurfaceWhite else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BottomIconAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
        Icon(imageVector = icon, contentDescription = label, tint = TextSecondary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
