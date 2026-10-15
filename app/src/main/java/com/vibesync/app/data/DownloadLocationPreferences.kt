package com.vibesync.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Null (the default) means downloads go to app-private storage, same as always. A non-null
 *  value is a persisted SAF tree URI string (from [android.content.Intent.ACTION_OPEN_DOCUMENT_TREE]
 *  via `ActivityResultContracts.OpenDocumentTree`) the user picked in Profile -> Download
 *  location -- only applies to downloads started *after* it's set, existing downloads stay put. */
class DownloadLocationPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _customTreeUri = MutableStateFlow(prefs.getString(KEY_TREE_URI, null))
    val customTreeUri: StateFlow<String?> = _customTreeUri.asStateFlow()

    fun setCustomTreeUri(uriString: String?) {
        prefs.edit().apply {
            if (uriString != null) putString(KEY_TREE_URI, uriString) else remove(KEY_TREE_URI)
        }.apply()
        _customTreeUri.value = uriString
    }

    private companion object {
        const val KEY_TREE_URI = "download_tree_uri"
    }
}
