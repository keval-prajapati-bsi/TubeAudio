package com.vibesync.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val DELIMITER = "|~|"

/** First-launch "what do you like to listen to?" picks -- languages and moods/genres, used to
 *  build the Home screen's "Recommended" search query (see [buildRecommendedQuery]). Plain
 *  SharedPreferences: a couple of small string sets and a completed flag, no need for Room/
 *  DataStore. */
class RecommendationPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("recommendations", Context.MODE_PRIVATE)

    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean(KEY_COMPLETED, false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _selectedLanguages = MutableStateFlow(readSet(KEY_LANGUAGES))
    val selectedLanguages: StateFlow<Set<String>> = _selectedLanguages.asStateFlow()

    private val _selectedGenres = MutableStateFlow(readSet(KEY_GENRES))
    val selectedGenres: StateFlow<Set<String>> = _selectedGenres.asStateFlow()

    /** Called both on first-launch onboarding and later edits from the Profile screen -- either
     *  way it (re)marks onboarding as done. */
    fun savePreferences(languages: Set<String>, genres: Set<String>) {
        prefs.edit()
            .putBoolean(KEY_COMPLETED, true)
            .putString(KEY_LANGUAGES, languages.joinToString(DELIMITER))
            .putString(KEY_GENRES, genres.joinToString(DELIMITER))
            .apply()
        _selectedLanguages.value = languages
        _selectedGenres.value = genres
        _onboardingCompleted.value = true
    }

    fun skipOnboarding() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
        _onboardingCompleted.value = true
    }

    /** No preferences set (skipped, or nothing picked) falls back to a general trending query
     *  rather than an empty/broken search. */
    fun buildRecommendedQuery(): String {
        val terms = _selectedGenres.value + _selectedLanguages.value
        return if (terms.isEmpty()) "trending songs india" else terms.joinToString(" ") + " songs"
    }

    private fun readSet(key: String): Set<String> =
        prefs.getString(key, null)?.split(DELIMITER)?.filter { it.isNotBlank() }?.toSet() ?: emptySet()

    private companion object {
        const val KEY_COMPLETED = "onboarding_completed"
        const val KEY_LANGUAGES = "languages"
        const val KEY_GENRES = "genres"
    }
}
