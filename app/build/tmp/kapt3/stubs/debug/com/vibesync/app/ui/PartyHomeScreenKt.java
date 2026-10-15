package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\b\u0010\u0006\u001a\u00020\u0007H\u0003\u00a8\u0006\b"}, d2 = {"PartyHomeScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "Lkotlin/Function0;", "partyFieldColors", "Landroidx/compose/material3/TextFieldColors;", "app_debug"})
public final class PartyHomeScreenKt {
    
    /**
     * Reached via the bottom nav's Menu -> Party Room entry. Two independent actions -- start your
     * own party (get a shareable code) or join one someone else started (type their code) -- rather
     * than one combined form, since they're genuinely different flows with different inputs.
     */
    @androidx.compose.runtime.Composable()
    public static final void PartyHomeScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final androidx.compose.material3.TextFieldColors partyFieldColors() {
        return null;
    }
}