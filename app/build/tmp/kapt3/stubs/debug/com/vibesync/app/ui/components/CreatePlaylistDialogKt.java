package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b\b\u0010\t\u001a|\u0010\n\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072d\u0010\f\u001a`\u0012\u0013\u0012\u00110\u000e\u00a2\u0006\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0010\u0012\u0015\u0012\u0013\u0018\u00010\u000e\u00a2\u0006\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0011\u0012\u0015\u0012\u0013\u0018\u00010\u000e\u00a2\u0006\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u00110\u0013\u00a2\u0006\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u00010\rH\u0007\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0015"}, d2 = {"ColorSwatch", "", "color", "Landroidx/compose/ui/graphics/Color;", "isSelected", "", "onClick", "Lkotlin/Function0;", "ColorSwatch-ek8zF_U", "(JZLkotlin/jvm/functions/Function0;)V", "CreatePlaylistDialog", "onDismiss", "onConfirm", "Lkotlin/Function4;", "", "Lkotlin/ParameterName;", "name", "description", "coverImageUri", "", "themeColorArgb", "app_debug"})
public final class CreatePlaylistDialogKt {
    
    /**
     * "Create playlist": cover photo (system photo picker, no permission needed), name,
     * optional description, and a theme-color swatch pick -- matches the real Figma file's own
     * "Create Playlist" screen, which is explicitly a bottom sheet (upload cover / name / description
     * / "Select Theme Accent" / Create Playlist button, in that order), not a centered dialog. All of
     * it is optional except the name.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void CreatePlaylistDialog(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function4<? super java.lang.String, ? super java.lang.String, ? super java.lang.String, ? super java.lang.Integer, kotlin.Unit> onConfirm) {
    }
}