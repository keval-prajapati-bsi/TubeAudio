package com.vibesync.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** This app has no accounts/login -- just a locally-editable display name shown on the Profile
 *  screen and the Dashboard's "Good evening, {name}" greeting. Plain SharedPreferences, same as
 *  [ThemePreferences], since it's a couple of strings. */
class ProfilePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("profile", Context.MODE_PRIVATE)

    private val _displayName = MutableStateFlow(prefs.getString(KEY_NAME, null) ?: DEFAULT_NAME)
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _subtitle = MutableStateFlow(prefs.getString(KEY_SUBTITLE, null) ?: DEFAULT_SUBTITLE)
    val subtitle: StateFlow<String> = _subtitle.asStateFlow()

    fun setProfile(name: String, subtitle: String) {
        val trimmedName = name.trim().ifBlank { DEFAULT_NAME }
        val trimmedSubtitle = subtitle.trim().ifBlank { DEFAULT_SUBTITLE }
        prefs.edit().putString(KEY_NAME, trimmedName).putString(KEY_SUBTITLE, trimmedSubtitle).apply()
        _displayName.value = trimmedName
        _subtitle.value = trimmedSubtitle
    }

    private companion object {
        const val KEY_NAME = "display_name"
        const val KEY_SUBTITLE = "subtitle"
        const val DEFAULT_NAME = "You"
        const val DEFAULT_SUBTITLE = "Personal listening"
    }
}
