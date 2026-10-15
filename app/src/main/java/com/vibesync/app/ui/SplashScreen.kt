package com.vibesync.app.ui

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBlack = Color(0xFF0D0C13)
private val LogoVioletLight = Color(0xFFB69CFF)
private val LogoVioletDark = Color(0xFF6C4CF0)
private val LogoMagenta = Color(0xFFE23F8E)

/**
 * Shown once on cold start, before [TubeAudioApp] -- a VibeSync-branded animated intro (logo
 * mark scales/fades in, wordmark follows) rather than the Android 12+ system SplashScreen API,
 * which only supports a static icon and can't give the "loading animation" that was asked for.
 * Deliberately theme-invariant (fixed near-black background) rather than following the user's
 * light/dark app theme -- this is a brand moment, not app UI. Only shown once per process (not
 * replayed on rotation) -- see [MainActivity]'s `SplashState` object.
 *
 * The logo mark is a single clean glyph -- a gradient ring plus a violet-to-magenta disc with a
 * centered play triangle, no secondary icon competing for attention -- deliberately simplified
 * from an earlier version that also overlaid a separate music-note badge, which read as cluttered
 * at actual launcher-icon size; this same mark is what `ic_launcher_foreground.xml` uses too, so
 * the home-screen icon and this splash are visually the same logo, not two different ones.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var animateIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateIn = true
        delay(1600)
        onFinished()
    }

    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.5f,
        animationSpec = tween(durationMillis = 650, easing = EaseOutBack),
        label = "logoScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "logoAlpha"
    )
    val wordmarkAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 350),
        label = "wordmarkAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(SplashBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            VibeSyncLogoMark(
                size = 140.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.graphicsLayer { this.alpha = wordmarkAlpha }) {
                Text(
                    text = "VIBE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "SYNC",
                    color = LogoMagenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun VibeSyncLogoMark(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = this.size.minDimension * 0.055f
            drawArc(
                brush = Brush.sweepGradient(listOf(LogoVioletDark, LogoMagenta, LogoVioletLight, LogoVioletDark)),
                startAngle = -110f,
                sweepAngle = 300f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Box(
            modifier = Modifier
                .size(size * 0.56f)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(LogoVioletLight, LogoVioletDark))),
            contentAlignment = Alignment.Center
        ) {
            // A plain white glyph reads more like a real app-icon play button than the previous
            // near-black one, which mostly disappeared into the disc's darker gradient end.
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.26f)
            )
        }
    }
}
