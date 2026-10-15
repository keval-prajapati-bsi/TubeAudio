package com.vibesync.app.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.vibesync.app.extractor.Chapter
import com.vibesync.app.extractor.ResolvedVideo
import com.vibesync.app.extractor.VideoResolutionException
import com.vibesync.app.extractor.VideoStreamResolver
import com.vibesync.app.playback.VideoPlayerController
import com.vibesync.app.ui.theme.NavyPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Full-screen video playback with a YouTube-style resolution picker -- the part of the original
 *  podcast ask deliberately deferred to its own round, now built for real. Uses a self-contained
 *  [VideoPlayerController] (its own plain ExoPlayer, not the app's shared audio [PlayerController])
 *  so nothing about this screen can disturb normal audio-only playback state; callers are
 *  responsible for pausing the audio player before opening this (see
 *  [MainViewModel.openVideoPlayer]) so the two don't play over each other. */
@Composable
fun VideoPlayerScreen(videoUrl: String, title: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val haptics = LocalHapticFeedback.current
    var resolved by remember { mutableStateOf<ResolvedVideo?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var isLandscape by remember { mutableStateOf(false) }

    val controller = remember { VideoPlayerController(context) }
    // Reverts orientation back to whatever the rest of the app expects the moment this screen
    // closes -- without this, rotating to watch a video would leave every other screen stuck in
    // landscape too. MainActivity declares orientation as a handled config change (see its own
    // comment) specifically so this actually works smoothly instead of recreating the Activity.
    DisposableEffect(Unit) {
        onDispose {
            controller.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(videoUrl) {
        try {
            val video = withContext(Dispatchers.IO) { VideoStreamResolver.resolve(videoUrl) }
            resolved = video
            controller.play(video.qualities.first(), startPositionMs = 0L)
        } catch (e: VideoResolutionException) {
            loadError = e.message
        } catch (e: Exception) {
            loadError = "Couldn't load this video (${e.message ?: e::class.simpleName})."
        }
    }

    BackHandler(onBack = onClose)

    val uiState by controller.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (resolved != null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = controller.exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (loadError == null && resolved == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (loadError != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(loadError ?: "", color = Color.White, modifier = Modifier.padding(24.dp))
                    TextButton(onClick = onClose) { Text("Close") }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = title,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
            Box {
                IconButton(onClick = { showQualityMenu = true }, enabled = resolved != null) {
                    Icon(Icons.Filled.HighQuality, contentDescription = "Resolution", tint = Color.White)
                }
                DropdownMenu(expanded = showQualityMenu, onDismissRequest = { showQualityMenu = false }) {
                    resolved?.qualities?.forEach { quality ->
                        DropdownMenuItem(
                            text = { Text(quality.label + if (quality.isMuxed) "" else " HD") },
                            trailingIcon = {
                                if (quality == uiState.currentQuality) Icon(Icons.Filled.Check, contentDescription = null)
                            },
                            onClick = {
                                showQualityMenu = false
                                if (quality != uiState.currentQuality) controller.play(quality)
                            }
                        )
                    }
                }
            }
            // Manual rotate/fullscreen toggle -- doesn't just wait on the device's own auto-rotate
            // (which may be locked off) the way a plain physical-rotation-only approach would; this
            // forces landscape for a real "watch fullscreen" option regardless of that setting, the
            // same control every mainstream video app offers.
            IconButton(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isLandscape = !isLandscape
                activity?.requestedOrientation = if (isLandscape) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }) {
                Icon(
                    imageVector = if (isLandscape) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                    contentDescription = if (isLandscape) "Exit fullscreen" else "Rotate to fullscreen",
                    tint = Color.White
                )
            }
        }

        val chapters = resolved?.chapters.orEmpty()
        if (chapters.isNotEmpty()) {
            VideoChapterRow(
                chapters = chapters,
                positionMs = uiState.positionMs,
                onJumpTo = { controller.seekTo(it) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(top = 56.dp)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(vertical = 8.dp)
            )
        }
    }
}

/** Same chip-row concept as the audio player's chapter markers (see `NowPlayingScreen.kt`'s
 *  `ChapterRow`) -- video playback was missing this entirely even though the underlying YouTube
 *  chapter data was always available, it just wasn't being read here. Styled for the dark video
 *  overlay rather than reusing that private composable directly. */
@Composable
private fun VideoChapterRow(chapters: List<Chapter>, positionMs: Long, onJumpTo: (Long) -> Unit, modifier: Modifier = Modifier) {
    val positionSeconds = (positionMs / 1000L).toInt()
    val activeIndex = chapters.indexOfLast { it.startTimeSeconds <= positionSeconds }
    LazyRow(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(chapters.size) { index ->
            val chapter = chapters[index]
            val isActive = index == activeIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) NavyPrimary else Color.White.copy(alpha = 0.16f))
                    .clickable { onJumpTo(chapter.startTimeSeconds * 1000L) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
