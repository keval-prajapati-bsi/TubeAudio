package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000T\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\u001a&\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bH\u0003\u001a@\u0010\f\u001a\u00020\u00052\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0018\u0010\u0013\u001a\u0014\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00050\u0014H\u0003\u001a\u001e\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bH\u0007\u001a&\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bH\u0003\u001a\u0010\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u0011H\u0002\u001a\u0010\u0010\u001f\u001a\u00020\t2\u0006\u0010 \u001a\u00020!H\u0002\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006\""}, d2 = {"DbScaleColumnWidth", "Landroidx/compose/ui/unit/Dp;", "F", "GraphHeight", "CircleIconButton", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "contentDescription", "", "onClick", "Lkotlin/Function0;", "EqualizerGraph", "bands", "", "Lcom/vibesync/app/playback/EqualizerBand;", "minLevel", "", "maxLevel", "onLevelChange", "Lkotlin/Function2;", "EqualizerScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "PresetButton", "label", "isSelected", "", "dbLabel", "level", "formatBandFrequency", "hz", "", "app_debug"})
public final class EqualizerScreenKt {
    private static final float GraphHeight = 0.0F;
    private static final float DbScaleColumnWidth = 0.0F;
    
    /**
     * Matches the reference mockup: a connected-dot EQ curve (drag any dot up/down to shape the
     * sound) rather than a bank of vertical sliders -- the previous version used rotated Sliders as
     * a robustness tradeoff; this is the fidelity pass the user asked for. Band count/frequencies
     * and the dB range are still read live from the device rather than hardcoded to the mockup's
     * 9-band/60Hz-16kHz/+-12dB layout, since real hardware varies (see
     * [com.vibesync.app.playback.EqualizerController]'s doc for why).
     */
    @androidx.compose.runtime.Composable()
    public static final void EqualizerScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CircleIconButton(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String contentDescription, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * A draggable EQ curve: bands are plotted at equal x-spacing (dot i at the center of the i-th of
     * [n] equal-width slots) so the frequency-label row below can reuse the exact same equal-weight
     * layout and stay pixel-aligned with the dots above it, without needing to hand-measure text
     * widths. Press-and-drag anywhere on the plot updates whichever band is nearest that x position.
     */
    @androidx.compose.runtime.Composable()
    private static final void EqualizerGraph(java.util.List<com.vibesync.app.playback.EqualizerBand> bands, short minLevel, short maxLevel, kotlin.jvm.functions.Function2<? super java.lang.Short, ? super java.lang.Short, kotlin.Unit> onLevelChange) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PresetButton(java.lang.String label, boolean isSelected, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    private static final java.lang.String dbLabel(short level) {
        return null;
    }
    
    private static final java.lang.String formatBandFrequency(int hz) {
        return null;
    }
}