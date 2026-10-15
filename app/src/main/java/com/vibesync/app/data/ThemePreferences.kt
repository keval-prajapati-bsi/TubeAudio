package com.vibesync.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** Theme mode, plus an optional accent-color override (single "Accent color" swatch pick) and an
 *  optional secondary-color override (set alongside the primary when a two-tone "More themes"
 *  gradient preset is picked instead of a single swatch). Both null means "use this theme's own
 *  built-in colors" -- see [com.vibesync.app.ui.theme.applyThemeColors]. Plain SharedPreferences,
 *  same reasoning as before: small flat state, no need for DataStore. */
class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        prefs.getString(KEY_THEME_MODE, null)?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: legacyDefault()
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    private val _accentColorArgb = MutableStateFlow(
        if (prefs.contains(KEY_ACCENT)) prefs.getInt(KEY_ACCENT, 0) else null
    )
    val accentColorArgb: StateFlow<Int?> = _accentColorArgb.asStateFlow()

    private val _secondaryColorArgb = MutableStateFlow(
        if (prefs.contains(KEY_ACCENT_SECONDARY)) prefs.getInt(KEY_ACCENT_SECONDARY, 0) else null
    )
    val secondaryColorArgb: StateFlow<Int?> = _secondaryColorArgb.asStateFlow()

    /** [primaryArgb] null resets to this theme's own default accent (and clears any secondary
     *  override too, since a two-tone preset only makes sense together with a primary). */
    fun setAccent(primaryArgb: Int?, secondaryArgb: Int? = null) {
        val editor = prefs.edit()
        if (primaryArgb != null) editor.putInt(KEY_ACCENT, primaryArgb) else editor.remove(KEY_ACCENT)
        if (primaryArgb != null && secondaryArgb != null) {
            editor.putInt(KEY_ACCENT_SECONDARY, secondaryArgb)
        } else {
            editor.remove(KEY_ACCENT_SECONDARY)
        }
        editor.apply()
        _accentColorArgb.value = primaryArgb
        _secondaryColorArgb.value = if (primaryArgb != null) secondaryArgb else null
    }

    // Old boolean "dark_mode" pref, from before System-theme support existed -- read once as the
    // starting point for anyone upgrading, so their previous explicit choice isn't silently reset.
    // V32 establishes the midnight player identity as the default for fresh installs. Existing
    // users retain their explicit preference, including an older light-mode choice.
    private fun legacyDefault(): ThemeMode =
        if (prefs.contains(KEY_LEGACY_DARK_MODE)) {
            if (prefs.getBoolean(KEY_LEGACY_DARK_MODE, false)) ThemeMode.DARK else ThemeMode.LIGHT
        } else {
            ThemeMode.DARK
        }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_ACCENT = "accent_color_argb"
        const val KEY_ACCENT_SECONDARY = "accent_color_secondary_argb"
        const val KEY_LEGACY_DARK_MODE = "dark_mode"
    }
}
