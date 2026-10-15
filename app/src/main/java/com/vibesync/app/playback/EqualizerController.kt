package com.vibesync.app.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import com.vibesync.app.data.EqualizerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class EqualizerBand(val index: Short, val centerFreqHz: Int, val level: Short)

/** A *named-curve* preset -- not the device's own built-in [Equalizer] presets (their names/count
 *  are vendor-dependent, e.g. plenty of devices don't ship anything called "Bass" since that's
 *  properly a separate [BassBoost] effect, not an EQ curve). [shape] is 9 points spanning a
 *  nominal 60Hz-to-16kHz range as fractions of the device's actual level range in -1f..1f --
 *  [EqualizerController.applyPreset] resamples this onto however many real bands the attached
 *  device reports, so the same 5 named presets work identically regardless of whether a given
 *  phone's Equalizer effect has 5 bands or 6. */
data class EqualizerPreset(val name: String, val shape: List<Float>)

val EqualizerPresets = listOf(
    EqualizerPreset("Flat", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    EqualizerPreset("Pop", listOf(-0.2f, 0.1f, 0.4f, 0.5f, 0.3f, -0.1f, -0.2f, -0.1f, 0.1f)),
    EqualizerPreset("Rock", listOf(0.5f, 0.3f, -0.1f, -0.3f, -0.1f, 0.2f, 0.4f, 0.4f, 0.3f)),
    EqualizerPreset("Bass", listOf(0.9f, 0.8f, 0.5f, 0.1f, -0.1f, -0.2f, -0.2f, -0.1f, 0f)),
    EqualizerPreset("Classical", listOf(0.2f, 0.1f, 0f, 0f, -0.1f, -0.1f, 0f, 0.1f, 0.2f))
)

data class EqualizerUiState(
    val isAvailable: Boolean = false,
    val isEnabled: Boolean = false,
    val bands: List<EqualizerBand> = emptyList(),
    val minLevel: Short = -1500,
    val maxLevel: Short = 1500,
    val selectedPresetName: String? = null,
    val bassBoostAvailable: Boolean = false,
    val bassBoostStrength: Short = 0
)

/**
 * Wraps the real system [Equalizer] (and, separately, [BassBoost]) AudioFx effects -- this is a
 * genuine DSP equalizer, not a UI-only mock: band levels actually change the audio ExoPlayer
 * outputs. Must [attach] to a stable `audioSessionId` (see [PlaybackService], which forces one
 * via `AudioManager.generateAudioSessionId()` before playback starts, rather than relying on
 * ExoPlayer's own lazily-assigned one) -- the effect only affects audio routed through that
 * exact session.
 *
 * A device's actual band count/frequencies/level range are hardware+vendor-determined (commonly
 * 5-6 bands, not a fixed 9), so this deliberately doesn't hardcode a band layout -- the UI is
 * built from whatever [attach] reports. [EqualizerPresets] are custom-defined curves (not the
 * device's own built-in presets, whose names aren't guaranteed) resampled onto the real band
 * count, so "Rock" etc. behave consistently across devices.
 */
class EqualizerController(private val preferences: EqualizerPreferences) {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    private val _state = MutableStateFlow(EqualizerUiState())
    val state: StateFlow<EqualizerUiState> = _state.asStateFlow()

    fun attach(audioSessionId: Int) {
        release()

        val eq = try {
            Equalizer(0, audioSessionId)
        } catch (_: Exception) {
            _state.value = EqualizerUiState(isAvailable = false)
            return
        }
        equalizer = eq

        val range = eq.bandLevelRange
        val minLevel = range.getOrElse(0) { -1500 }
        val maxLevel = range.getOrElse(1) { 1500 }
        val bandCount = eq.numberOfBands.toInt()

        val bands = (0 until bandCount).map { i ->
            val band = i.toShort()
            val freqHz = eq.getCenterFreq(band) / 1000
            val savedLevel = preferences.getBandLevel(band) ?: eq.getBandLevel(band)
            eq.setBandLevel(band, savedLevel)
            EqualizerBand(band, freqHz, savedLevel)
        }

        val enabled = preferences.isEnabled()
        eq.setEnabled(enabled)

        val bass = try {
            BassBoost(0, audioSessionId)
        } catch (_: Exception) {
            null
        }
        bassBoost = bass
        val bassSupported = bass?.strengthSupported == true
        if (bass != null && bassSupported) bass.setEnabled(enabled)

        _state.value = EqualizerUiState(
            isAvailable = true,
            isEnabled = enabled,
            bands = bands,
            minLevel = minLevel,
            maxLevel = maxLevel,
            selectedPresetName = preferences.getSelectedPresetName(),
            bassBoostAvailable = bassSupported,
            bassBoostStrength = if (bassSupported) bass!!.roundedStrength else 0
        )
    }

    fun setEnabled(enabled: Boolean) {
        equalizer?.setEnabled(enabled)
        if (_state.value.bassBoostAvailable) bassBoost?.setEnabled(enabled)
        preferences.setEnabled(enabled)
        _state.value = _state.value.copy(isEnabled = enabled)
    }

    fun setBandLevel(band: Short, level: Short) {
        val eq = equalizer ?: return
        val clamped = level.coerceIn(_state.value.minLevel, _state.value.maxLevel)
        eq.setBandLevel(band, clamped)
        preferences.setBandLevel(band, clamped)
        preferences.setSelectedPresetName(null)
        _state.value = _state.value.copy(
            bands = _state.value.bands.map { if (it.index == band) it.copy(level = clamped) else it },
            selectedPresetName = null
        )
    }

    fun applyPreset(preset: EqualizerPreset) {
        val eq = equalizer ?: return
        val current = _state.value
        if (current.bands.isEmpty()) return
        val maxAbs = maxOf(abs(current.minLevel.toInt()), abs(current.maxLevel.toInt()))
        val bandCount = current.bands.size
        val updated = current.bands.mapIndexed { i, existing ->
            val t = if (bandCount == 1) 0.5f else i / (bandCount - 1).toFloat()
            val factor = sampleShape(preset.shape, t)
            val level = (factor * maxAbs).toInt().coerceIn(current.minLevel.toInt(), current.maxLevel.toInt()).toShort()
            eq.setBandLevel(existing.index, level)
            preferences.setBandLevel(existing.index, level)
            existing.copy(level = level)
        }
        preferences.setSelectedPresetName(preset.name)
        _state.value = current.copy(bands = updated, selectedPresetName = preset.name)
    }

    fun reset() {
        EqualizerPresets.firstOrNull { it.name == "Flat" }?.let { applyPreset(it) }
        setBassBoostStrength(0)
    }

    fun setBassBoostStrength(strength: Short) {
        val clamped = strength.coerceIn(0, 1000)
        bassBoost?.setStrength(clamped)
        _state.value = _state.value.copy(bassBoostStrength = clamped)
    }

    fun release() {
        equalizer?.release()
        equalizer = null
        bassBoost?.release()
        bassBoost = null
    }

    private fun sampleShape(shape: List<Float>, t: Float): Float {
        val pos = t * (shape.size - 1)
        val i0 = pos.toInt().coerceIn(0, shape.size - 1)
        val i1 = (i0 + 1).coerceAtMost(shape.size - 1)
        val frac = pos - i0
        return shape[i0] * (1 - frac) + shape[i1] * frac
    }
}
