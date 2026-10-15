package com.vibesync.app.data;

/**
 * Live "do we currently have a validated internet connection" signal -- used to show a friendly
 * "you're offline" state instead of a raw network-error snackbar on launch (Home's Recommended/
 * Trending fetches), and to skip firing those fetches at all while offline. Application-scoped,
 * registers one [ConnectivityManager.NetworkCallback] for the whole process lifetime.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000f\u001a\u00020\u0007H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000e\u00a8\u0006\u0010"}, d2 = {"Lcom/vibesync/app/data/NetworkMonitor;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_isOnline", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "callback", "Landroid/net/ConnectivityManager$NetworkCallback;", "connectivityManager", "Landroid/net/ConnectivityManager;", "isOnline", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "currentlyOnline", "app_debug"})
public final class NetworkMonitor {
    @org.jetbrains.annotations.NotNull()
    private final android.net.ConnectivityManager connectivityManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isOnline = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isOnline = null;
    @org.jetbrains.annotations.NotNull()
    private final android.net.ConnectivityManager.NetworkCallback callback = null;
    
    public NetworkMonitor(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isOnline() {
        return null;
    }
    
    private final boolean currentlyOnline() {
        return false;
    }
}