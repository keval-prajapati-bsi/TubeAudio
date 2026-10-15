package com.vibesync.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.vibesync.app.data.ThemeMode

@Composable
fun TubeAudioTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColorArgb: Int? = null,
    secondaryColorArgb: Int? = null,
    content: @Composable () -> Unit
) {
    val darkMode = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Mutates the module-level color vars in Color.kt; every existing call site
    // reads them inside a @Composable function, so they recompose automatically
    // on toggle without needing to migrate to MaterialTheme.colorScheme lookups.
    applyThemeColors(darkMode, accentColorArgb?.let { Color(it) }, secondaryColorArgb?.let { Color(it) })

    val colorScheme = if (darkMode) {
        darkColorScheme(
            primary = NavyPrimary,
            onPrimary = OnAccent,
            primaryContainer = NavyPrimaryDark,
            onPrimaryContainer = TextPrimary,
            secondary = AccentTeal,
            onSecondary = OnAccent,
            background = PageBackground,
            onBackground = TextPrimary,
            surface = SurfaceContainer,
            onSurface = TextPrimary,
            surfaceVariant = BackgroundWhite,
            onSurfaceVariant = TextSecondary,
            outline = CardBorder,
            error = ErrorRed
        )
    } else {
        lightColorScheme(
            primary = NavyPrimary,
            onPrimary = OnAccent,
            primaryContainer = NavyPrimaryDark,
            onPrimaryContainer = TextPrimary,
            secondary = AccentTeal,
            onSecondary = OnAccent,
            background = PageBackground,
            onBackground = TextPrimary,
            surface = SurfaceContainer,
            onSurface = TextPrimary,
            surfaceVariant = BackgroundWhite,
            onSurfaceVariant = TextSecondary,
            outline = CardBorder,
            error = ErrorRed
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkMode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkMode
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TubeAudioTypography,
        content = content
    )
}
