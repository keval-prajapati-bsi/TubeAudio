package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a|\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\u0010\b\u0002\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\f2\u0010\b\u0002\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\f2$\u0010\u000e\u001a \u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\u0007\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\u0007\u0012\u0004\u0012\u00020\u00050\u000fH\u0007\u001a&\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\n2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\fH\u0003\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Genres", "", "", "Languages", "OnboardingScreen", "", "initialLanguages", "", "initialGenres", "isEditMode", "", "onBack", "Lkotlin/Function0;", "onSkip", "onDone", "Lkotlin/Function2;", "PreferenceChip", "label", "selected", "onClick", "app_debug"})
public final class OnboardingScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> Languages = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> Genres = null;
    
    /**
     * Shown once on first launch (before the main app), and reusable from Profile ("Music
     * preferences") to edit picks later -- [isEditMode] just swaps the button/back-arrow chrome,
     * the picker itself is identical either way. Selections drive the Home screen's "Recommended"
     * section (see [com.vibesync.app.data.RecommendationPreferences.buildRecommendedQuery]).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    public static final void OnboardingScreen(@org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> initialLanguages, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> initialGenres, boolean isEditMode, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSkip, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.util.Set<java.lang.String>, ? super java.util.Set<java.lang.String>, kotlin.Unit> onDone) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PreferenceChip(java.lang.String label, boolean selected, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}