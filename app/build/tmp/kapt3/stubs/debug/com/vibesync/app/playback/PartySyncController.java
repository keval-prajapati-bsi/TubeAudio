package com.vibesync.app.playback;

/**
 * Makes every device in a party hear the same thing at the same position -- including someone who
 * joins mid-song. The party's Firestore doc stores *when* (server timestamp) the current track
 * started and *at what position*; every client (this one included) computes
 * `expected = startPositionMs + (now - playbackStartedAt)` rather than the host broadcasting a
 * position update every second. **This is a broadcast, not a point-to-point relay** -- it costs
 * exactly the same whether 2 or 10 (or 100) people are in the party, since every device just reads
 * the same one Firestore document and does the same local math; nothing here gets more expensive
 * or less precise as the party grows. On the host's device only, this also auto-advances to the
 * next approved queue item once the current track actually finishes.
 *
 * Deliberately does not calibrate for clock skew between devices (no server-time offset
 * handshake) -- assumes each phone's system clock is reasonably accurate via normal network time
 * sync. A device with a manually-set wrong clock will drift; that's a known, disclosed
 * simplification, not something this class tries to solve.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0017H\u0082@\u00a2\u0006\u0002\u0010 J\r\u0010!\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\u0002\u0010\"J\u000e\u0010#\u001a\u00020\u001eH\u0082@\u00a2\u0006\u0002\u0010$J\u0010\u0010%\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u0017H\u0002J\u0016\u0010&\u001a\u00020\u001e2\u0006\u0010\'\u001a\u00020\u0014H\u0082@\u00a2\u0006\u0002\u0010(J\u0016\u0010)\u001a\u00020\t2\u0006\u0010\'\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010(J\u0016\u0010*\u001a\u00020\t2\u0006\u0010\'\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010(J\u001e\u0010+\u001a\u00020\u001e2\u0006\u0010\'\u001a\u00020\u00142\u0006\u0010,\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010-J\u001e\u0010.\u001a\u00020\u001e2\u0006\u0010\'\u001a\u00020\u00142\u0006\u0010/\u001a\u00020\t2\u0006\u00100\u001a\u000201J\u0006\u00102\u001a\u00020\u001eJ\u001e\u00103\u001a\u00020\u001e2\u0006\u0010\'\u001a\u00020\u00142\u0006\u0010,\u001a\u00020\u001aH\u0082@\u00a2\u0006\u0002\u0010-J\b\u00104\u001a\u00020\u001eH\u0002J\u000e\u00105\u001a\u00020\u001eH\u0082@\u00a2\u0006\u0002\u0010$J\f\u00106\u001a\u000207*\u00020\u001aH\u0002R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0012R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2 = {"Lcom/vibesync/app/playback/PartySyncController;", "", "partyRepository", "Lcom/vibesync/app/data/party/PartyRepository;", "playerController", "Lcom/vibesync/app/playback/PlayerController;", "(Lcom/vibesync/app/data/party/PartyRepository;Lcom/vibesync/app/playback/PlayerController;)V", "_isSyncing", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "isHostDevice", "isSyncing", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "job", "Lkotlinx/coroutines/Job;", "lastAppliedPlaybackStartedAtMillis", "", "Ljava/lang/Long;", "lastAppliedVideoId", "", "lastSeenLocalVideoId", "latestParty", "Lcom/vibesync/app/data/party/PartyDoc;", "latestQueue", "", "Lcom/vibesync/app/data/party/PartyQueueItem;", "partyCode", "transitionInProgress", "applyPartyState", "", "party", "(Lcom/vibesync/app/data/party/PartyDoc;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "currentExpectedPositionMs", "()Ljava/lang/Long;", "driftCheckLoop", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "expectedPosition", "hostAdvanceLoop", "code", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "playNext", "playPrevious", "playQueueItem", "item", "(Ljava/lang/String;Lcom/vibesync/app/data/party/PartyQueueItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "start", "isHost", "scope", "Lkotlinx/coroutines/CoroutineScope;", "stop", "transitionForward", "updatePartyPrefetch", "watchLocalTransitions", "toTrackEntity", "Lcom/vibesync/app/data/db/TrackEntity;", "app_debug"})
public final class PartySyncController {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.party.PartyRepository partyRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.playback.PlayerController playerController = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job job;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.vibesync.app.data.party.PartyDoc latestParty;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private volatile java.util.List<com.vibesync.app.data.party.PartyQueueItem> latestQueue;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastAppliedVideoId;
    
    /**
     * Changes on *every* discrete party event -- play, pause, seek, and track-change all write a
     * fresh server timestamp here (see `PartyRepository.setPlaybackState`/`setCurrentTrack`), so
     * comparing this single field is enough to detect "something just happened, snap exactly"
     * without needing separate bookkeeping for play-state vs. seek-while-playing.
     */
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastAppliedPlaybackStartedAtMillis;
    private boolean transitionInProgress = false;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String partyCode;
    private boolean isHostDevice = false;
    
    /**
     * The videoId [playerController] is actually showing right now, tracked independently of
     * [lastAppliedVideoId] (which only changes when *this class* drives a change) -- lets
     * [applyPartyState] tell "the player already got here on its own, via a local gapless
     * auto-advance" apart from "this is a genuinely new track nobody's device has seen yet".
     */
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastSeenLocalVideoId;
    
    /**
     * True from the moment a party is joined/created until this device has actually caught up to
     * wherever the party currently is (or immediately, if nothing's playing yet) -- drives the
     * "Joining party..." screen so nobody hears a stale/out-of-sync moment of audio while the
     * stream is still resolving.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isSyncing = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSyncing = null;
    
    public PartySyncController(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.party.PartyRepository partyRepository, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.playback.PlayerController playerController) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSyncing() {
        return null;
    }
    
    public final void start(@org.jetbrains.annotations.NotNull()
    java.lang.String code, boolean isHost, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
    }
    
    public final void stop() {
    }
    
    /**
     * The party's live expected position right now, or null if there's no active party/track --
     * exposed so `MainViewModel` can catch a device back up to the party (e.g. when a non-host
     * member resumes local playback after pausing it) without duplicating this formula.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long currentExpectedPositionMs() {
        return null;
    }
    
    private final java.lang.Object applyPartyState(com.vibesync.app.data.party.PartyDoc party, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Runs on every device (not just the host): re-checks drift on a timer, independent of
     * whatever Firestore snapshot events happen to arrive. [applyPartyState] only ever runs
     * reactively -- if a track plays uninterrupted for minutes with no pause/seek/track-change,
     * no new snapshot arrives and no drift check happens in that whole window, even though
     * consumer audio hardware clocks aren't perfectly matched between phones and *will* drift
     * apart slowly during exactly that kind of long, uninterrupted stretch. This is very likely
     * the actual explanation for a "slight audio mismatch between two users" report that shows up
     * gradually rather than right after some obvious event -- there was previously no mechanism
     * correcting for it at all outside of a discrete event happening to occur.
     */
    private final java.lang.Object driftCheckLoop(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Tells [playerController] what the party queue's next approved item is right now, so it can
     * keep it pre-buffered on the live ExoPlayer timeline (see
     * [PlayerController.setPartyUpcoming]). Called on every party-doc and every queue update --
     * cheap and idempotent, [setPartyUpcoming] itself no-ops if nothing actually changed.
     */
    private final void updatePartyPrefetch() {
    }
    
    /**
     * Watches [playerController]'s own reported "now playing" videoId for a change that *we*
     * didn't just cause via [applyPartyState]'s reload path -- that's exactly what a natural
     * gapless auto-advance onto the prefetched next track (see [setPartyUpcoming]) looks like
     * from here. Two things happen the moment that's detected: bookkeeping catches up immediately
     * ([lastAppliedVideoId] set right away, not only once Firestore's own snapshot for this track
     * eventually arrives) so [applyPartyState] recognizes "already there" and reconciles position
     * instead of reloading the same track a second time from the network; and on the host's
     * device specifically, the party doc gets updated right now instead of waiting on
     * [hostAdvanceLoop]'s slower poll -- which is what makes every other device's own local
     * auto-advance (they're all independently prefetching the same next queue item) land at
     * nearly the same real-world moment instead of however many seconds behind the poll interval.
     */
    private final java.lang.Object watchLocalTransitions(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Host-only: watches for the current track actually finishing (not a manual pause) and moves
     * on to the next approved queue item.
     */
    private final java.lang.Object hostAdvanceLoop(java.lang.String code, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Forward transitions (direct-pick, auto-advance, skip-next): retires whatever was current
     * to PLAYED (if it corresponds to a real queue item), then makes [item] current. [item]'s own
     * status is left as-is -- direct-pick and skip-next both always target an already-APPROVED
     * item, so there's nothing to normalize on the way in (unlike [playPrevious], which does need
     * to un-PLAY the item it's bringing back).
     */
    private final java.lang.Object transitionForward(java.lang.String code, com.vibesync.app.data.party.PartyQueueItem item, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Host action: jump straight to any approved queue item, skipping whatever order it was in.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object playQueueItem(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.party.PartyQueueItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Host action (also used by the auto-advance loop): move to the next approved item by
     * position. Returns false if the queue is empty, so the caller can stop playback instead.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object playNext(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Host action: go back to whatever played immediately before the current track. Deliberately
     * NOT built on [transitionForward] -- going backward needs to *un-PLAY* the track it's
     * returning to (so a later `playPrevious` doesn't pick the same track again) while *also*
     * re-approving the track being left behind at the front of "Up next" (not marking it PLAYED,
     * which is what the forward path would do) so skipping forward again lands right back where
     * you were. Trying to force both directions through one shared function is what caused an
     * early draft of this to silently overwrite its own writes -- kept as two separate,
     * intentionally-not-unified code paths instead. Returns false if there's no history yet.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object playPrevious(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    private final com.vibesync.app.data.db.TrackEntity toTrackEntity(com.vibesync.app.data.party.PartyQueueItem $this$toTrackEntity) {
        return null;
    }
    
    private final long expectedPosition(com.vibesync.app.data.party.PartyDoc party) {
        return 0L;
    }
}