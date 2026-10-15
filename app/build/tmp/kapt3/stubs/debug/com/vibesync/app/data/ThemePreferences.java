package com.vibesync.app.data;

/**
 * Theme mode, plus an optional accent-color override (single "Accent color" swatch pick) and an
 * optional secondary-color override (set alongside the primary when a two-tone "More themes"
 * gradient preset is picked instead of a single swatch). Both null means "use this theme's own
 * built-in colors" -- see [com.vibesync.app.ui.theme.applyThemeColors]. Plain SharedPreferences,
 * same reasoning as before: small flat state, no need for DataStore.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0016\u001a\u00020\nH\u0002J!\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\u001bJ\u000e\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\nR\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000e\u00a8\u0006\u001f"}, d2 = {"Lcom/vibesync/app/data/ThemePreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_accentColorArgb", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_secondaryColorArgb", "_themeMode", "Lcom/vibesync/app/data/ThemeMode;", "accentColorArgb", "Lkotlinx/coroutines/flow/StateFlow;", "getAccentColorArgb", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "secondaryColorArgb", "getSecondaryColorArgb", "themeMode", "getThemeMode", "legacyDefault", "setAccent", "", "primaryArgb", "secondaryArgb", "(Ljava/lang/Integer;Ljava/lang/Integer;)V", "setThemeMode", "mode", "Companion", "app_debug"})
public final class ThemePreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.data.ThemeMode> _themeMode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.ThemeMode> themeMode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _accentColorArgb = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> accentColorArgb = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _secondaryColorArgb = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> secondaryColorArgb = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_THEME_MODE = "theme_mode";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_ACCENT = "accent_color_argb";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_ACCENT_SECONDARY = "accent_color_secondary_argb";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_LEGACY_DARK_MODE = "dark_mode";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.ThemePreferences.Companion Companion = null;
    
    public ThemePreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.ThemeMode> getThemeMode() {
        return null;
    }
    
    public final void setThemeMode(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.ThemeMode mode) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getAccentColorArgb() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getSecondaryColorArgb() {
        return null;
    }
    
    /**
     * [primaryArgb] null resets to this theme's own default accent (and clears any secondary
     * override too, since a two-tone preset only makes sense together with a primary).
     */
    public final void setAccent(@org.jetbrains.annotations.Nullable()
    java.lang.Integer primaryArgb, @org.jetbrains.annotations.Nullable()
    java.lang.Integer secondaryArgb) {
    }
    
    private final com.vibesync.app.data.ThemeMode legacyDefault() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/vibesync/app/data/ThemePreferences$Companion;", "", "()V", "KEY_ACCENT", "", "KEY_ACCENT_SECONDARY", "KEY_LEGACY_DARK_MODE", "KEY_THEME_MODE", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}