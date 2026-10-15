package com.vibesync.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MAX_RECENT_QUERIES = 10

// A separator unlikely to appear in a typed search query -- simpler than JSON for a flat
// "list of strings in one SharedPreferences string".
private const val QUERY_DELIMITER = "|~|"

/** Recent YouTube song-search queries, newest first -- plain SharedPreferences, same
 *  reasoning as [ThemePreferences] (one small piece of state, not worth DataStore). */
class SearchHistoryPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    private val _recentQueries = MutableStateFlow(loadQueries())
    val recentQueries: StateFlow<List<String>> = _recentQueries.asStateFlow()

    fun addQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val updated = (listOf(trimmed) + _recentQueries.value.filterNot { it.equals(trimmed, ignoreCase = true) })
            .take(MAX_RECENT_QUERIES)
        save(updated)
    }

    fun removeQuery(query: String) {
        save(_recentQueries.value.filterNot { it == query })
    }

    fun clear() {
        save(emptyList())
    }

    private fun save(queries: List<String>) {
        _recentQueries.value = queries
        prefs.edit().putString(KEY, queries.joinToString(QUERY_DELIMITER)).apply()
    }

    private fun loadQueries(): List<String> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return raw.split(QUERY_DELIMITER).filter { it.isNotBlank() }
    }

    private companion object {
        const val KEY = "recent_queries"
    }
}
