package com.vibesync.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Small animated "equalizer" bars -- the Spotify/Apple-Music-style "this row is playing right
 *  now" indicator, shown overlaid on a track row's thumbnail when its videoId matches what's
 *  actually loaded in the player. Each bar animates independently (staggered durations/delays)
 *  so they don't move in lockstep, which is what sells the "live" feel. */
@Composable
fun SoundBarsIndicator(modifier: Modifier = Modifier, barColor: Color = MaterialTheme.colorScheme.primary) {
    val transition = rememberInfiniteTransition(label = "soundBars")
    val heights = listOf(
        barHeight(transition, durationMs = 480, delayMs = 0),
        barHeight(transition, durationMs = 620, delayMs = 90),
        barHeight(transition, durationMs = 520, delayMs = 180)
    )
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        heights.forEachIndexed { index, height ->
            if (index > 0) androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(2.dp))
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height)
                    .clip(RoundedCornerShape(1.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun barHeight(transition: androidx.compose.animation.core.InfiniteTransition, durationMs: Int, delayMs: Int): Dp {
    val fraction by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, delayMillis = delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar"
    )
    return (fraction * 14).dp
}
