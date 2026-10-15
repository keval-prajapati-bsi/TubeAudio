package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A small flat "vinyl record" motif -- solid black outer ring with a smaller colored center
 *  label, deliberately always black regardless of light/dark theme (it's rendered on top of a
 *  saturated gradient/color card, not the page background, so a fixed black reads correctly in
 *  both themes rather than needing to flip). Used on Trending's chart cards and the Dashboard's
 *  Recently Played tiles. */
@Composable
fun VinylDisk(modifier: Modifier = Modifier, size: Dp = 28.dp, labelColor: Color = Color.White) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.34f)
                .clip(CircleShape)
                .background(labelColor.copy(alpha = 0.9f))
        )
    }
}
