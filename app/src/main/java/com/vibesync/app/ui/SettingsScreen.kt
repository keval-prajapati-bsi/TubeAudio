package com.vibesync.app.ui

import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.ThemeMode
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

private val AccentColorOptions = listOf(
    Color(0xFFE0507E),
    Color(0xFF8B5FBF),
    Color(0xFF9B59B6),
    Color(0xFF4A90D9),
    Color(0xFF2FA8E0),
    Color(0xFF3DBFB8)
)

private data class GradientPreset(val start: Color, val end: Color)

private val MoreThemePresets = listOf(
    GradientPreset(Color(0xFFFFA53D), Color(0xFFFF6B35)),
    GradientPreset(Color(0xFF8B5FBF), Color(0xFF6A3DBF)),
    GradientPreset(Color(0xFFFF5F6D), Color(0xFFFFC371)),
    GradientPreset(Color(0xFFB983FF), Color(0xFF7A5FFF)),
    GradientPreset(Color(0xFF56AB2F), Color(0xFFA8E063))
)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val accentColorArgb by viewModel.accentColorArgb.collectAsStateWithLifecycle()
    var showAdvanced by remember { mutableStateOf(false) }

    BackHandler(enabled = showAdvanced) { showAdvanced = false }

    if (showAdvanced) {
        AdvancedSettingsScreen(onBack = { showAdvanced = false })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                ThemeModeSelector(
                    selected = themeMode,
                    onSelect = { viewModel.setThemeMode(it) }
                )
            }

            HorizontalDivider()

            Text(
                text = "Accent color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DefaultAccentSwatch(
                    isSelected = accentColorArgb == null,
                    onClick = { viewModel.setAccent(null) }
                )
                AccentColorOptions.forEach { color ->
                    AccentSwatch(
                        color = color,
                        isSelected = accentColorArgb == color.toArgb(),
                        onClick = { viewModel.setAccent(color.toArgb()) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 20.dp))

            Text(
                text = "More themes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(MoreThemePresets) { preset ->
                    Box(
                        modifier = Modifier
                            .size(width = 64.dp, height = 88.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.verticalGradient(listOf(preset.start, preset.end)))
                            .clickable { viewModel.setAccent(preset.start.toArgb(), preset.end.toArgb()) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvanced = true }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Advance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun ThemeModeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp)
    ) {
        ThemeMode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) SurfaceWhite else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun AccentSwatch(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) Icon(Icons.Filled.Check, contentDescription = "Selected", tint = SurfaceWhite)
    }
}

@Composable
private fun DefaultAccentSwatch(isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(NavyPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Autorenew,
            contentDescription = "Default accent color",
            tint = SurfaceWhite,
            modifier = Modifier.size(16.dp)
        )
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
private fun AdvancedSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember(context) {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Headphones, contentDescription = null, tint = NavyPrimary)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "TubeAudio",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = versionName?.let { "Version $it" } ?: "Version unavailable",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Plays a YouTube link's audio in the background, offline downloads, " +
                    "playlists, and search -- built for personal listening.\n\n" +
                    "Note: resolving audio this way isn't an officially sanctioned YouTube API " +
                    "(the same approach open-source players like NewPipe use), so it's meant for " +
                    "your own private use, not redistribution.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
