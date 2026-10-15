package com.vibesync.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.playback.EqualizerBand
import com.vibesync.app.playback.EqualizerPresets
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import kotlin.math.roundToInt

/** Matches the reference mockup: a connected-dot EQ curve (drag any dot up/down to shape the
 *  sound) rather than a bank of vertical sliders -- the previous version used rotated Sliders as
 *  a robustness tradeoff; this is the fidelity pass the user asked for. Band count/frequencies
 *  and the dB range are still read live from the device rather than hardcoded to the mockup's
 *  9-band/60Hz-16kHz/+-12dB layout, since real hardware varies (see
 *  [com.vibesync.app.playback.EqualizerController]'s doc for why). */
@Composable
fun EqualizerScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state by viewModel.equalizerState.collectAsStateWithLifecycle()
    var showPresetMenu by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = "Equalizer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.isEnabled,
                onCheckedChange = { viewModel.setEqualizerEnabled(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = SurfaceWhite, checkedTrackColor = NavyPrimary)
            )
        }

        if (!state.isAvailable) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "This device doesn't expose an audio equalizer effect, or nothing has " +
                        "played yet this session -- try again once a song is loaded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
            return
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPresetMenu = true }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.selectedPresetName ?: "Custom",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Choose preset", tint = TextSecondary)
            }
            DropdownMenu(expanded = showPresetMenu, onDismissRequest = { showPresetMenu = false }) {
                EqualizerPresets.forEach { preset ->
                    DropdownMenuItem(
                        text = { Text(preset.name) },
                        onClick = {
                            showPresetMenu = false
                            viewModel.applyEqualizerPreset(preset)
                        }
                    )
                }
            }
        }
        HorizontalDivider(color = CardBorder)

        Spacer(modifier = Modifier.height(20.dp))

        EqualizerGraph(
            bands = state.bands,
            minLevel = state.minLevel,
            maxLevel = state.maxLevel,
            onLevelChange = { band, level -> viewModel.setEqualizerBandLevel(band, level) }
        )

        if (state.bassBoostAvailable) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Bass boost", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Slider(
                value = state.bassBoostStrength.toFloat(),
                onValueChange = { viewModel.setBassBoostStrength(it.toInt().toShort()) },
                valueRange = 0f..1000f,
                colors = SliderDefaults.colors(thumbColor = NavyPrimary, activeTrackColor = NavyPrimary)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Presets",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EqualizerPresets.forEach { preset ->
                PresetButton(
                    label = preset.name,
                    isSelected = state.selectedPresetName == preset.name,
                    onClick = { viewModel.applyEqualizerPreset(preset) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CircleIconButton(
                icon = Icons.Filled.RestartAlt,
                contentDescription = "Reset to flat",
                onClick = { viewModel.resetEqualizer() }
            )
            Spacer(modifier = Modifier.width(24.dp))
            CircleIconButton(
                icon = Icons.Filled.HelpOutline,
                contentDescription = "About the equalizer",
                onClick = { showInfoDialog = true }
            )
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = { TextButton(onClick = { showInfoDialog = false }) { Text("Got it") } },
            title = { Text("About this equalizer") },
            text = {
                Text(
                    "Bands and their frequencies come straight from your device's own audio " +
                        "hardware, so the count and spacing here can differ from other phones. " +
                        "The presets (Flat/Pop/Rock/Bass/Classical) are curve shapes built for " +
                        "this app rather than your device's own factory presets, so they'll " +
                        "always be available no matter what hardware you're on."
                )
            }
        )
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(BorderStroke(1.dp, CardBorder), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = TextSecondary)
    }
}

private val GraphHeight = 190.dp
private val DbScaleColumnWidth = 38.dp

/** A draggable EQ curve: bands are plotted at equal x-spacing (dot i at the center of the i-th of
 *  [n] equal-width slots) so the frequency-label row below can reuse the exact same equal-weight
 *  layout and stay pixel-aligned with the dots above it, without needing to hand-measure text
 *  widths. Press-and-drag anywhere on the plot updates whichever band is nearest that x position. */
@Composable
private fun EqualizerGraph(
    bands: List<EqualizerBand>,
    minLevel: Short,
    maxLevel: Short,
    onLevelChange: (Short, Short) -> Unit
) {
    if (bands.isEmpty()) return
    val lineColor = NavyPrimary
    val gridColor = TextSecondary.copy(alpha = 0.18f)
    val dotFill = NavyPrimary

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().height(GraphHeight)) {
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(bands.size, minLevel, maxLevel) {
                        val n = bands.size
                        val range = (maxLevel - minLevel).toFloat()
                        val slotWidth = size.width.toFloat() / n
                        val canvasHeight = size.height.toFloat()
                        fun apply(offset: Offset) {
                            if (n == 0 || range <= 0f) return
                            val slot = (offset.x / slotWidth).toInt().coerceIn(0, n - 1)
                            val fraction = 1f - (offset.y / canvasHeight).coerceIn(0f, 1f)
                            val level = (minLevel + fraction * range).roundToInt().toShort()
                            onLevelChange(bands[slot].index, level)
                        }
                        detectDragGestures(
                            onDragStart = { apply(it) },
                            onDrag = { change, _ -> apply(change.position) }
                        )
                    }
            ) {
                val n = bands.size
                val stepX = size.width / n
                val midY = size.height / 2f
                val range = (maxLevel - minLevel).toFloat()

                fun yFor(level: Short): Float {
                    if (range <= 0f) return midY
                    val fraction = (level - minLevel) / range
                    return size.height - fraction * size.height
                }

                drawLine(gridColor, Offset(0f, midY), Offset(size.width, midY), strokeWidth = 1.dp.toPx())

                val points = bands.mapIndexed { i, band -> Offset(stepX * (i + 0.5f), yFor(band.level)) }

                points.forEach { p ->
                    drawLine(
                        color = gridColor,
                        start = Offset(p.x, 0f),
                        end = Offset(p.x, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f))
                    )
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = lineColor,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                points.forEach { p ->
                    drawCircle(color = dotFill, radius = 5.5.dp.toPx(), center = p)
                    drawCircle(color = Color.White, radius = 2.2.dp.toPx(), center = p)
                }
            }

            Column(
                modifier = Modifier.width(DbScaleColumnWidth).fillMaxHeight().padding(start = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(dbLabel(maxLevel), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.weight(1f))
                Text("0dB", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.weight(1f))
                Text(dbLabel(minLevel), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            bands.forEach { band ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = formatBandFrequency(band.centerFreqHz),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(DbScaleColumnWidth))
        }
    }
}

@Composable
private fun PresetButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) NavyPrimary else Color.Transparent)
            .border(
                BorderStroke(1.dp, if (isSelected) NavyPrimary else CardBorder),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) SurfaceWhite else TextSecondary
        )
    }
}

private fun dbLabel(level: Short): String {
    val db = level / 100
    return if (db > 0) "+${db}dB" else "${db}dB"
}

private fun formatBandFrequency(hz: Int): String =
    if (hz >= 1000) "${hz / 1000}K" else "$hz"
