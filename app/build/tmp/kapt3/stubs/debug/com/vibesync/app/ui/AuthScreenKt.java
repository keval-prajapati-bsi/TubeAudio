package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a$\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0007\u001a\b\u0010\t\u001a\u00020\nH\u0003\u00a8\u0006\u000b"}, d2 = {"AuthModeSelector", "", "selected", "Lcom/vibesync/app/ui/AuthMode;", "onSelect", "Lkotlin/Function1;", "AuthScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "authFieldColors", "Landroidx/compose/material3/TextFieldColors;", "app_debug"})
public final class AuthScreenKt {
    
    /**
     * Shown after the splash screen whenever there's no signed-in Firebase user -- gates the whole
     * app behind a real account, which the party feature needs (host/requester identity, per-party
     * membership). Styled to match the rest of the app (same OutlinedTextField/Button look as
     * [SearchScreen]'s paste-link card) rather than looking like a bolted-on auth SDK screen.
     */
    @androidx.compose.runtime.Composable()
    public static final void AuthScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final androidx.compose.material3.TextFieldColors authFieldColors() {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AuthModeSelector(com.vibesync.app.ui.AuthMode selected, kotlin.jvm.functions.Function1<? super com.vibesync.app.ui.AuthMode, kotlin.Unit> onSelect) {
    }
}