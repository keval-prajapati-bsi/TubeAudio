package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\u001e\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001a\b\u0010\f\u001a\u00020\rH\u0003\u00a8\u0006\u000e"}, d2 = {"DiscoveredRoomRow", "", "room", "Lcom/vibesync/app/data/localparty/DiscoveredRoom;", "enabled", "", "onClick", "Lkotlin/Function0;", "LocalRoomHomeScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "localFieldColors", "Landroidx/compose/material3/TextFieldColors;", "app_debug"})
public final class LocalRoomHomeScreenKt {
    
    /**
     * Reached via the bottom nav's Menu -> Local Room entry. The LAN counterpart to Party Room --
     * no Firebase, no internet, no account/code needed beyond being on the same WiFi -- host a room
     * (advertised via local network discovery) or browse rooms other phones nearby are already
     * hosting. See `localparty/LocalPartyHost.kt`'s doc comment for how this actually works
     * end-to-end.
     */
    @androidx.compose.runtime.Composable()
    public static final void LocalRoomHomeScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DiscoveredRoomRow(com.vibesync.app.data.localparty.DiscoveredRoom room, boolean enabled, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final androidx.compose.material3.TextFieldColors localFieldColors() {
        return null;
    }
}