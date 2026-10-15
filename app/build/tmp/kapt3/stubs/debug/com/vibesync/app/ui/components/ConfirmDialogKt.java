package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a>\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u00a8\u0006\t"}, d2 = {"ConfirmDialog", "", "title", "", "message", "confirmLabel", "onConfirm", "Lkotlin/Function0;", "onDismiss", "app_debug"})
public final class ConfirmDialogKt {
    
    /**
     * Shared confirmation prompt for every destructive action app-wide (delete a playlist/track,
     * leave/close a party or local room, kick/remove a member, and anything else that reaches for
     * this dialog) -- the confirm button is always styled red, and the haptic tap on confirm here is
     * a single edit that covers every one of those call sites at once, one of the highest-leverage
     * spots in the whole app to add feedback for "you just did something that can't be undone".
     */
    @androidx.compose.runtime.Composable()
    public static final void ConfirmDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    java.lang.String confirmLabel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}