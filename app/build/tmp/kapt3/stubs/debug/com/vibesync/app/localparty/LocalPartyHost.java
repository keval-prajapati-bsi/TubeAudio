package com.vibesync.app.localparty;

/**
 * Runs entirely on the host's own phone: a raw TCP [ServerSocket] on an OS-assigned free port,
 * advertised on the local network via NSD so guests can find it without typing an IP -- no
 * internet, no Firebase, nothing outside this WiFi network required at all. This is a genuinely
 * separate peer, not a scaled-down version of the Firebase Party feature.
 *
 * Protocol is newline-delimited JSON (one object per line -- plain JSON alone has no message
 * framing over a raw byte stream, so a delimiter is required): guests send `hello`/`requestSong`/
 * `leave`; the host only ever sends a `state` message (the FULL current [LocalRoomState] snapshot,
 * every time anything changes) or `kicked`. Sending the whole snapshot each time rather than diffs
 * is deliberately simple -- there's no Firestore-style targeted-write concept over a raw socket,
 * and a LAN party's member/queue lists are small enough that this costs nothing meaningful.
 *
 * **Known, disclosed simplification**: the local socket has no authentication/pairing step --
 * anyone who can see the mDNS advertisement on the same WiFi can connect and request songs. This
 * matches the trust model of the feature (a room you're hosting for people already in the same
 * physical space/network), not a public internet-facing service, but it's worth knowing this isn't
 * hardened the way the Firebase party (real Firebase Auth + security rules) is.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a4\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u001dH\u0082@\u00a2\u0006\u0002\u0010%J\u000e\u0010&\u001a\u00020#2\u0006\u0010\'\u001a\u00020(J\u000e\u0010)\u001a\u00020#H\u0082@\u00a2\u0006\u0002\u0010*J\u0016\u0010+\u001a\u00020#2\u0006\u0010,\u001a\u00020-H\u0082@\u00a2\u0006\u0002\u0010.J\u000e\u0010/\u001a\u00020#2\u0006\u00100\u001a\u00020\u0005J\u0010\u00101\u001a\u00020#2\u0006\u00102\u001a\u000203H\u0002J\u000e\u00104\u001a\u00020#2\u0006\u00105\u001a\u00020\u0005J\u0016\u00106\u001a\u00020#2\u0006\u00107\u001a\u00020\u0005H\u0082@\u00a2\u0006\u0002\u00108J\u0014\u00109\u001a\u00020#2\f\u0010:\u001a\b\u0012\u0004\u0012\u00020(0;J\u0018\u0010<\u001a\u00020#2\u0006\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020@H\u0002J:\u0010A\u001a\u00020#2\u0006\u0010B\u001a\u00020\u00052\u0006\u0010C\u001a\u00020\u00052\b\u0010D\u001a\u0004\u0018\u00010\u00052\u0006\u0010E\u001a\u00020\u00052\u0006\u0010F\u001a\u00020G2\b\u0010H\u001a\u0004\u0018\u00010\u0005J\u0016\u0010I\u001a\u00020#2\u0006\u0010J\u001a\u00020K2\u0006\u0010L\u001a\u00020GJ\u0006\u0010M\u001a\u00020#J\u0006\u0010N\u001a\u00020#J\u001c\u0010O\u001a\u00020#2\u0012\u0010P\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0QH\u0002R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0013\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u000fR\u000e\u0010 \u001a\u00020!X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006R"}, d2 = {"Lcom/vibesync/app/localparty/LocalPartyHost;", "", "context", "Landroid/content/Context;", "hostDisplayName", "", "roomName", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V", "_errorMessage", "Lkotlinx/coroutines/flow/MutableStateFlow;", "_state", "Lcom/vibesync/app/data/localparty/LocalRoomState;", "errorMessage", "Lkotlinx/coroutines/flow/StateFlow;", "getErrorMessage", "()Lkotlinx/coroutines/flow/StateFlow;", "guests", "", "Lcom/vibesync/app/localparty/ConnectedGuest;", "hostId", "getHostId", "()Ljava/lang/String;", "nsdManager", "Landroid/net/nsd/NsdManager;", "registrationListener", "Landroid/net/nsd/NsdManager$RegistrationListener;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "serverSocket", "Ljava/net/ServerSocket;", "state", "getState", "stateLock", "Lkotlinx/coroutines/sync/Mutex;", "acceptLoop", "", "server", "(Ljava/net/ServerSocket;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addToQueue", "item", "Lcom/vibesync/app/data/localparty/LocalQueueItem;", "broadcastState", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "handleGuest", "socket", "Ljava/net/Socket;", "(Ljava/net/Socket;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kick", "memberId", "registerNsd", "port", "", "removeFromQueue", "itemId", "removeGuest", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reorderQueue", "newOrder", "", "sendTo", "writer", "Ljava/io/OutputStreamWriter;", "json", "Lorg/json/JSONObject;", "setCurrentTrack", "videoId", "title", "thumbnailUrl", "url", "durationSeconds", "", "fromQueueItemId", "setPlaybackState", "isPlaying", "", "positionMs", "start", "stop", "updateState", "transform", "Lkotlin/Function1;", "app_debug"})
public final class LocalPartyHost {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String hostId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.sync.Mutex stateLock = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.vibesync.app.localparty.ConnectedGuest> guests = null;
    @org.jetbrains.annotations.Nullable()
    private java.net.ServerSocket serverSocket;
    @org.jetbrains.annotations.NotNull()
    private final android.net.nsd.NsdManager nsdManager = null;
    @org.jetbrains.annotations.Nullable()
    private android.net.nsd.NsdManager.RegistrationListener registrationListener;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.data.localparty.LocalRoomState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.localparty.LocalRoomState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    
    public LocalPartyHost(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String hostDisplayName, @org.jetbrains.annotations.NotNull()
    java.lang.String roomName) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHostId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.localparty.LocalRoomState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getErrorMessage() {
        return null;
    }
    
    public final void start() {
    }
    
    private final void registerNsd(int port) {
    }
    
    private final java.lang.Object acceptLoop(java.net.ServerSocket server, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object handleGuest(java.net.Socket socket, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object removeGuest(java.lang.String id, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object broadcastState(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void sendTo(java.io.OutputStreamWriter writer, org.json.JSONObject json) {
    }
    
    private final void updateState(kotlin.jvm.functions.Function1<? super com.vibesync.app.data.localparty.LocalRoomState, com.vibesync.app.data.localparty.LocalRoomState> transform) {
    }
    
    /**
     * Sets what's playing now, freezing a fresh start timestamp as the sync anchor -- same
     * formula every guest's [LocalPartyClient] uses to compute its own expected position (see
     * [LocalRoomState]'s doc comment). Removes the item from the queue if it came from there
     * (an ad-hoc host play passes null).
     */
    public final void setCurrentTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String url, long durationSeconds, @org.jetbrains.annotations.Nullable()
    java.lang.String fromQueueItemId) {
    }
    
    public final void setPlaybackState(boolean isPlaying, long positionMs) {
    }
    
    public final void addToQueue(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.localparty.LocalQueueItem item) {
    }
    
    public final void reorderQueue(@org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> newOrder) {
    }
    
    public final void removeFromQueue(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId) {
    }
    
    public final void kick(@org.jetbrains.annotations.NotNull()
    java.lang.String memberId) {
    }
    
    public final void stop() {
    }
}