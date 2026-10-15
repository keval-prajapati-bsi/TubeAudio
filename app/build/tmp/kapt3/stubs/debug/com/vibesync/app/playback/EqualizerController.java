package com.vibesync.app.playback;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\n\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u0016J\u0006\u0010\u0017\u001a\u00020\u0011J\u0006\u0010\u0018\u001a\u00020\u0011J\u001e\u0010\u0019\u001a\u00020\u001a2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001c2\u0006\u0010\u001d\u001a\u00020\u001aH\u0002J\u0016\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 J\u000e\u0010\"\u001a\u00020\u00112\u0006\u0010#\u001a\u00020 J\u000e\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020&R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\'"}, d2 = {"Lcom/vibesync/app/playback/EqualizerController;", "", "preferences", "Lcom/vibesync/app/data/EqualizerPreferences;", "(Lcom/vibesync/app/data/EqualizerPreferences;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/vibesync/app/playback/EqualizerUiState;", "bassBoost", "Landroid/media/audiofx/BassBoost;", "equalizer", "Landroid/media/audiofx/Equalizer;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "applyPreset", "", "preset", "Lcom/vibesync/app/playback/EqualizerPreset;", "attach", "audioSessionId", "", "release", "reset", "sampleShape", "", "shape", "", "t", "setBandLevel", "band", "", "level", "setBassBoostStrength", "strength", "setEnabled", "enabled", "", "app_debug"})
public final class EqualizerController {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.EqualizerPreferences preferences = null;
    @org.jetbrains.annotations.Nullable()
    private android.media.audiofx.Equalizer equalizer;
    @org.jetbrains.annotations.Nullable()
    private android.media.audiofx.BassBoost bassBoost;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.playback.EqualizerUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.EqualizerUiState> state = null;
    
    public EqualizerController(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.EqualizerPreferences preferences) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.EqualizerUiState> getState() {
        return null;
    }
    
    public final void attach(int audioSessionId) {
    }
    
    public final void setEnabled(boolean enabled) {
    }
    
    public final void setBandLevel(short band, short level) {
    }
    
    public final void applyPreset(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.playback.EqualizerPreset preset) {
    }
    
    public final void reset() {
    }
    
    public final void setBassBoostStrength(short strength) {
    }
    
    public final void release() {
    }
    
    private final float sampleShape(java.util.List<java.lang.Float> shape, float t) {
        return 0.0F;
    }
}