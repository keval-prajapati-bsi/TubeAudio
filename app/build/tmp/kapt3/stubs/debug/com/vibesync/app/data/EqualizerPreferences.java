package com.vibesync.app.data;

/**
 * Persists the equalizer's on/off state and per-band levels (in millibels, the same unit
 * [android.media.audiofx.Equalizer] itself uses) so they survive app restarts and get
 * re-applied the moment a new playback session attaches -- see
 * [com.vibesync.app.playback.EqualizerController]. Plain SharedPreferences: a boolean and a
 * handful of small ints, no need for anything heavier.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\n\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\b\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\u0011J\b\u0010\u0012\u001a\u0004\u0018\u00010\tJ\u0006\u0010\u0013\u001a\u00020\u0014J\u0016\u0010\u0015\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u000bJ\u000e\u0010\u0017\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0014J\u0010\u0010\u0019\u001a\u00020\r2\b\u0010\u001a\u001a\u0004\u0018\u00010\tR\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/vibesync/app/data/EqualizerPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "bandKey", "", "band", "", "clearBandLevels", "", "bandCount", "", "getBandLevel", "(S)Ljava/lang/Short;", "getSelectedPresetName", "isEnabled", "", "setBandLevel", "level", "setEnabled", "enabled", "setSelectedPresetName", "name", "Companion", "app_debug"})
public final class EqualizerPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_ENABLED = "enabled";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_PRESET = "preset";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.EqualizerPreferences.Companion Companion = null;
    
    public EqualizerPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final boolean isEnabled() {
        return false;
    }
    
    public final void setEnabled(boolean enabled) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Short getBandLevel(short band) {
        return null;
    }
    
    public final void setBandLevel(short band, short level) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedPresetName() {
        return null;
    }
    
    public final void setSelectedPresetName(@org.jetbrains.annotations.Nullable()
    java.lang.String name) {
    }
    
    public final void clearBandLevels(int bandCount) {
    }
    
    private final java.lang.String bandKey(short band) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/vibesync/app/data/EqualizerPreferences$Companion;", "", "()V", "KEY_ENABLED", "", "KEY_PRESET", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}