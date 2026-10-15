package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import com.vibesync.app.ui.theme.SurfaceWhite

/** A filled circular play button -- the same visual language as the Playlist detail screen's big
 *  round Play button, just sized down for use as a "Play all" action in list headers (Search
 *  results, Trending). Deliberately not a plain flat [androidx.compose.material3.IconButton] so
 *  every "play this whole list" action looks consistent across the app. */
@Composable
fun RoundPlayButton(onClick: () -> Unit, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play all",
            tint = SurfaceWhite,
            // The triangle glyph has extra empty space on its right within its own bounding box,
            // so centering it by box math alone reads as shifted left -- nudge it back to center.
            modifier = Modifier.size(size * 0.55f).offset(x = size * 0.04f)
        )
    }
}
