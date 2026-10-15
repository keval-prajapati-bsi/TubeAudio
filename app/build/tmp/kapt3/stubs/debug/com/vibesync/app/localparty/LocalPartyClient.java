package com.vibesync.app.localparty;

/**
 * A guest's live connection to someone else's [LocalPartyHost] -- the client-side half of the
 * same newline-delimited-JSON protocol (see that class's doc comment). Read loop runs for the
 * whole life of the connection; [state] mirrors whatever the host last broadcast.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010 \u001a\u00020\u001dJ0\u0010!\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020\u00032\u0006\u0010#\u001a\u00020\u00032\b\u0010$\u001a\u0004\u0018\u00010\u00032\u0006\u0010%\u001a\u00020\u00032\u0006\u0010&\u001a\u00020\'J\u0010\u0010(\u001a\u00020\u001d2\u0006\u0010)\u001a\u00020*H\u0002R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000eR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000eR\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/vibesync/app/localparty/LocalPartyClient;", "", "displayName", "", "(Ljava/lang/String;)V", "_connectionError", "Lkotlinx/coroutines/flow/MutableStateFlow;", "_state", "Lcom/vibesync/app/data/localparty/LocalRoomState;", "_wasKicked", "", "connectionError", "Lkotlinx/coroutines/flow/StateFlow;", "getConnectionError", "()Lkotlinx/coroutines/flow/StateFlow;", "myId", "getMyId", "()Ljava/lang/String;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "socket", "Ljava/net/Socket;", "state", "getState", "wasKicked", "getWasKicked", "writer", "Ljava/io/OutputStreamWriter;", "connect", "", "room", "Lcom/vibesync/app/data/localparty/DiscoveredRoom;", "disconnect", "requestSong", "videoId", "title", "thumbnailUrl", "url", "durationSeconds", "", "send", "json", "Lorg/json/JSONObject;", "app_debug"})
public final class LocalPartyClient {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String myId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private java.net.Socket socket;
    @org.jetbrains.annotations.Nullable()
    private java.io.OutputStreamWriter writer;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.data.localparty.LocalRoomState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.localparty.LocalRoomState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _connectionError = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> connectionError = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _wasKicked = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> wasKicked = null;
    
    public LocalPartyClient(@org.jetbrains.annotations.NotNull()
    java.lang.String displayName) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMyId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.data.localparty.LocalRoomState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getConnectionError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getWasKicked() {
        return null;
    }
    
    public final void connect(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.localparty.DiscoveredRoom room) {
    }
    
    private final void send(org.json.JSONObject json) {
    }
    
    public final void requestSong(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String url, long durationSeconds) {
    }
    
    public final void disconnect() {
    }
}