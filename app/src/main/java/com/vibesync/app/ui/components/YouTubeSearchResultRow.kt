package com.vibesync.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vibesync.app.extractor.SearchResult
import com.vibesync.app.ui.formatDuration
import com.vibesync.app.ui.theme.AccentTeal
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary

/** Tapping the row plays the song immediately (no save required); the heart saves it to Liked
 *  Songs, and "+" opens the full "add to a specific playlist" picker -- three independent
 *  actions, matching "play it without needing to add it to the library first". */
@Composable
fun YouTubeSearchResultRow(
    result: SearchResult,
    isAdded: Boolean,
    isLiked: Boolean,
    onPlay: () -> Unit,
    onToggleLike: () -> Unit,
    onAdd: () -> Unit,
    onAddToQueue: () -> Unit = {},
    isCurrentlyPlaying: Boolean = false,
    isInQueue: Boolean = false,
    /** Null everywhere except the Podcasts tab -- video playback is a podcast-specific option,
     *  not something every search/trending row should offer. */
    onWatchVideo: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(if (isCurrentlyPlaying) Modifier.background(NavyPrimary.copy(alpha = 0.08f)) else Modifier)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onPlay()
            }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundWhite)
                .then(
                    if (isCurrentlyPlaying) Modifier.border(BorderStroke(1.5.dp, NavyPrimary), RoundedCornerShape(12.dp))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (result.thumbnailUrl != null) {
                AsyncImage(
                    model = result.thumbnailUrl,
                    contentDescription = result.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
            if (isCurrentlyPlaying) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyPrimary.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    SoundBarsIndicator(barColor = androidx.compose.ui.graphics.Color.White)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isCurrentlyPlaying) NavyPrimary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val subtitle = listOfNotNull(result.uploaderName, formatDuration(result.durationSeconds * 1000))
                .joinToString(" · ")
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggleLike()
        }) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) ErrorRed else TextSecondary
            )
        }

        IconButton(onClick = onAdd, enabled = !isAdded) {
            Icon(
                imageVector = if (isAdded) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = if (isAdded) "Added to a playlist" else "Add to playlist",
                tint = if (isAdded) AccentTeal else NavyPrimary
            )
        }

        IconButton(onClick = onAddToQueue, enabled = !isInQueue) {
            Icon(
                imageVector = Icons.Filled.QueueMusic,
                contentDescription = if (isInQueue) "Already in queue" else "Add to queue",
                tint = if (isInQueue) AccentTeal else TextSecondary
            )
        }

        if (onWatchVideo != null) {
            IconButton(onClick = onWatchVideo) {
                Icon(imageVector = Icons.Filled.SmartDisplay, contentDescription = "Watch video", tint = NavyPrimary)
            }
        }
    }
}
