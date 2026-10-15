package com.vibesync.app.data

import android.content.Context

/** Persists the equalizer's on/off state and per-band levels (in millibels, the same unit
 *  [android.media.audiofx.Equalizer] itself uses) so they survive app restarts and get
 *  re-applied the moment a new playback session attaches -- see
 *  [com.vibesync.app.playback.EqualizerController]. Plain SharedPreferences: a boolean and a
 *  handful of small ints, no need for anything heavier. */
class EqualizerPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("equalizer", Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun getBandLevel(band: Short): Short? {
        val key = bandKey(band)
        return if (prefs.contains(key)) prefs.getInt(key, 0).toShort() else null
    }

    fun setBandLevel(band: Short, level: Short) {
        prefs.edit().putInt(bandKey(band), level.toInt()).apply()
    }

    fun getSelectedPresetName(): String? = prefs.getString(KEY_PRESET, null)

    fun setSelectedPresetName(name: String?) {
        prefs.edit().putString(KEY_PRESET, name).apply()
    }

    fun clearBandLevels(bandCount: Int) {
        val editor = prefs.edit()
        for (band in 0 until bandCount) editor.remove(bandKey(band.toShort()))
        editor.remove(KEY_PRESET)
        editor.apply()
    }

    private fun bandKey(band: Short) = "band_$band"

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val KEY_PRESET = "preset"
    }
}
