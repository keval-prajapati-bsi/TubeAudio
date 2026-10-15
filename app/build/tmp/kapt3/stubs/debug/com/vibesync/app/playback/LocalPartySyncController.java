package com.vibesync.app.playback;

/**
 * The local room's equivalent of [PartySyncController] -- same expected-position formula
 * (`startPositionMs + (now - playbackStartedAtMillis)` while playing), same discrete-event/drift
 * correction shape, just fed by [LocalPartyHost]/[LocalPartyClient]'s in-memory/socket state
 * instead of Firestore snapshots. Both host and guest run the exact same [applyLocalState] against
 * their own local copy of the room state -- for the host that's [LocalPartyHost.state] itself (a
 * same-process `StateFlow`, so "the host reacts to its own broadcast" costs nothing and needs no
 * special-casing, exactly like the Firebase host reacting to its own echoed Firestore snapshot).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0016\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0016\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u0017H\u0082@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001bJ\u0016\u0010\u001c\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\u001d\u001a\u00020\rR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\tR\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/vibesync/app/playback/LocalPartySyncController;", "", "playerController", "Lcom/vibesync/app/playback/PlayerController;", "(Lcom/vibesync/app/playback/PlayerController;)V", "job", "Lkotlinx/coroutines/Job;", "lastAppliedPlaybackStartedAtMillis", "", "Ljava/lang/Long;", "lastAppliedVideoId", "", "applyLocalState", "", "state", "Lcom/vibesync/app/data/localparty/LocalRoomState;", "driftCheckLoop", "client", "Lcom/vibesync/app/localparty/LocalPartyClient;", "(Lcom/vibesync/app/localparty/LocalPartyClient;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "expectedPosition", "hostAdvanceLoop", "host", "Lcom/vibesync/app/localparty/LocalPartyHost;", "(Lcom/vibesync/app/localparty/LocalPartyHost;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startAsGuest", "scope", "Lkotlinx/coroutines/CoroutineScope;", "startAsHost", "stop", "app_debug"})
public final class LocalPartySyncController {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.PlayerController playerController = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job job;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastAppliedVideoId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastAppliedPlaybackStartedAtMillis;
    
    public LocalPartySyncController(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.playback.PlayerController playerController) {
        super();
    }
    
    public final void startAsHost(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.localparty.LocalPartyHost host, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
    }
    
    public final void startAsGuest(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.localparty.LocalPartyClient client, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
    }
    
    public final void stop() {
    }
    
    private final void applyLocalState(com.vibesync.app.data.localparty.LocalRoomState state) {
    }
    
    private final java.lang.Object driftCheckLoop(com.vibesync.app.localparty.LocalPartyClient client, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Host-only: watches for the current track actually finishing and moves on to the front of
     * the queue -- the poll-based mechanism the Firebase party used before its gapless fix (see
     * this class's doc comment for why the local room doesn't have that fix yet).
     */
    private final java.lang.Object hostAdvanceLoop(com.vibesync.app.localparty.LocalPartyHost host, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final long expectedPosition(com.vibesync.app.data.localparty.LocalRoomState state) {
        return 0L;
    }
}