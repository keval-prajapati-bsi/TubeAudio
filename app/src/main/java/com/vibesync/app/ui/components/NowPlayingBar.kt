package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vibesync.app.playback.PlaybackUiState
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.Dimens
import com.vibesync.app.ui.theme.ElevatedCardBackground
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.OnAccent
import com.vibesync.app.ui.theme.TextPrimary
import com.vibesync.app.ui.theme.TextSecondary

/** Docked bar: art + title/artist + a single play/pause button, no slider, no prev/next -- a
 *  floating glass card (spec: "Floating card, rounded corners") that now rides the same dark
 *  Elevated-Card layer as every other raised surface, rather than the older frozen
 *  light-theme-always capsule -- that freeze made sense against the old two-tone palette, but the
 *  whole point of this pass is one consistent premium dark system, so it's a deliberate reversal
 *  of that earlier call, not an oversight. Full transport (prev/next, scrubber, shuffle, repeat)
 *  is one tap away via [onExpand]. [onSkipNext]/[onSkipPrevious] stay in the signature so callers
 *  don't need to change, just unused here now. */
@Composable
fun NowPlayingBar(
    state: PlaybackUiState,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MediumGap, vertical = Dimens.SmallGap)
            .clip(RoundedCornerShape(Dimens.CornerCard))
            .clickable(onClick = onExpand),
        color = ElevatedCardBackground.copy(alpha = 0.94f),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = Dimens.SmallGap, horizontal = Dimens.MediumGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Thumbnail(state.thumbnailUrl)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.MediumGap)
            ) {
                Text(
                    text = state.title ?: "Nothing playing",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (state.title != null) {
                    Text(
                        text = state.queueLabel.ifBlank { "Now Playing" },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            val haptics = LocalHapticFeedback.current
            PlayPauseButton(state) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onTogglePlayPause()
            }
        }
    }
}

@Composable
private fun Thumbnail(url: String?) {
    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Dimens.CornerImage - 4.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Dimens.CornerImage - 4.dp))
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
        }
    }
}

/** A solid filled circle, not a bare icon -- the play/pause button is the single most-tapped
 *  control in the app, and a filled treatment (the convention every major music app's mini
 *  player uses) gives it real visual weight. */
@Composable
private fun PlayPauseButton(state: PlaybackUiState, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(NavyPrimary)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        if (state.isBuffering) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = OnAccent)
        } else {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = OnAccent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
