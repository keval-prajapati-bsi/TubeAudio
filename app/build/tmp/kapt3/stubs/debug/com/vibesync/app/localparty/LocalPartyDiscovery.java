package com.vibesync.app.localparty;

/**
 * Browses the local network for other phones' [LocalPartyHost]s via NSD -- the "here's a list of
 * rooms available in your local network" step from the original ask, before actually joining
 * one. [NsdManager.resolveService] (the classic, non-callback API) is deprecated on API 34 in
 * favor of [NsdManager.registerServiceInfoCallback], but still fully functional -- kept here for
 * simplicity since minSdk 26 needs to support it either way; a pre-existing style of deprecation
 * warning this app already has plenty of elsewhere (AutoMirrored icons, etc), not a functional
 * concern.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0018\u001a\u00020\u0019J\u0006\u0010\u001a\u001a\u00020\u0019R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012\u00a8\u0006\u001b"}, d2 = {"Lcom/vibesync/app/localparty/LocalPartyDiscovery;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_errorMessage", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_isDiscovering", "", "_rooms", "", "Lcom/vibesync/app/data/localparty/DiscoveredRoom;", "discoveryListener", "Landroid/net/nsd/NsdManager$DiscoveryListener;", "errorMessage", "Lkotlinx/coroutines/flow/StateFlow;", "getErrorMessage", "()Lkotlinx/coroutines/flow/StateFlow;", "isDiscovering", "nsdManager", "Landroid/net/nsd/NsdManager;", "rooms", "getRooms", "start", "", "stop", "app_debug"})
public final class LocalPartyDiscovery {
    @org.jetbrains.annotations.NotNull()
    private final android.net.nsd.NsdManager nsdManager = null;
    @org.jetbrains.annotations.Nullable()
    private android.net.nsd.NsdManager.DiscoveryListener discoveryListener;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.vibesync.app.data.localparty.DiscoveredRoom>> _rooms = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.localparty.DiscoveredRoom>> rooms = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isDiscovering = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isDiscovering = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    
    public LocalPartyDiscovery(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.localparty.DiscoveredRoom>> getRooms() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isDiscovering() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getErrorMessage() {
        return null;
    }
    
    public final void start() {
    }
    
    public final void stop() {
    }
}