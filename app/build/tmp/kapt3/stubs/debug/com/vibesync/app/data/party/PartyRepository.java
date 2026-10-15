package com.vibesync.app.data.party;

/**
 * Firestore-backed: `parties/{code}` (the code IS the doc id, so "join by code" is a direct doc
 * lookup, never a query -- no Firestore indexes needed anywhere in here by design), with
 * `parties/{code}/queue/{itemId}` and `parties/{code}/members/{uid}` subcollections. See
 * `FIRESTORE_RULES.md` in the project root for the security rules this data model assumes
 * (host-only approve/reorder/track-control/kick, any member can request/leave).
 *
 * Queue items are never deleted once approved -- they transition PENDING -> APPROVED -> PLAYED
 * (see [QueueItemStatus]) instead, which is what makes "go back one song" possible: it's just
 * "find the most recently PLAYED item and make it current again."
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002JD\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0014J@\u0010\u0015\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0017\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0018J&\u0010\u0019\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u001bJ&\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\n2\u0006\u0010\u001f\u001a\u00020\n2\u0006\u0010 \u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010!J\u001c\u0010\"\u001a\u00020\n2\n\u0010#\u001a\u00060$j\u0002`%2\u0006\u0010&\u001a\u00020\nH\u0002J\u001e\u0010\'\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010(\u001a\u00020\nH\u0082@\u00a2\u0006\u0002\u0010)J&\u0010*\u001a\u00020+2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010,\u001a\u00020\n2\u0006\u0010-\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010!J\u001e\u0010.\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010)J\u0016\u0010/\u001a\u0002002\u0006\u0010\t\u001a\u00020\n2\u0006\u0010,\u001a\u00020\nJ&\u00101\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u001e\u00102\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010)J\u0010\u00103\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\nH\u0002J\u001a\u00104\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002060\f052\u0006\u0010\t\u001a\u00020\nJ\u0016\u00107\u001a\n\u0012\u0006\u0012\u0004\u0018\u000108052\u0006\u0010\t\u001a\u00020\nJ\u001a\u00109\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020:0\f052\u0006\u0010\t\u001a\u00020\nJ\u0010\u0010;\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\nH\u0002J\b\u0010<\u001a\u00020\nH\u0002J\u001e\u0010=\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010)J\u001e\u0010>\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010)J$\u0010?\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\n0\fH\u0086@\u00a2\u0006\u0002\u0010AJ0\u0010B\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\r2\b\u0010C\u001a\u0004\u0018\u00010\n2\u0006\u0010D\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010EJ&\u0010F\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00112\u0006\u0010H\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010IR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006J"}, d2 = {"Lcom/vibesync/app/data/party/PartyRepository;", "", "()V", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "partiesRef", "Lcom/google/firebase/firestore/CollectionReference;", "addPlaylistToQueue", "Lcom/vibesync/app/data/party/PartyActionResult;", "code", "", "tracks", "", "Lcom/vibesync/app/data/db/TrackEntity;", "addedByUid", "addedByName", "autoApprove", "", "startPosition", "", "(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ZJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addToQueue", "track", "position", "(Ljava/lang/String;Lcom/vibesync/app/data/db/TrackEntity;Ljava/lang/String;Ljava/lang/String;ZJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "approveQueueItem", "itemId", "(Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createParty", "Lcom/vibesync/app/data/party/CreatePartyResult;", "hostUid", "hostName", "partyName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "friendlyFirestoreError", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "action", "isAlreadyQueued", "videoId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "joinParty", "Lcom/vibesync/app/data/party/JoinPartyResult;", "uid", "displayName", "kickMember", "leaveParty", "", "markApproved", "markPlayed", "membersRef", "observeMembers", "Lkotlinx/coroutines/flow/Flow;", "Lcom/vibesync/app/data/party/PartyMember;", "observeParty", "Lcom/vibesync/app/data/party/PartyDoc;", "observeQueue", "Lcom/vibesync/app/data/party/PartyQueueItem;", "queueRef", "randomCode", "rejectQueueItem", "removeQueueItem", "reorderQueue", "orderedItemIds", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCurrentTrack", "queueItemId", "startPositionMs", "(Ljava/lang/String;Lcom/vibesync/app/data/db/TrackEntity;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setPlaybackState", "isPlaying", "positionMs", "(Ljava/lang/String;ZJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class PartyRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.CollectionReference partiesRef = null;
    
    public PartyRepository() {
        super();
    }
    
    private final com.google.firebase.firestore.CollectionReference queueRef(java.lang.String code) {
        return null;
    }
    
    private final com.google.firebase.firestore.CollectionReference membersRef(java.lang.String code) {
        return null;
    }
    
    /**
     * A raw Firestore `PERMISSION_DENIED` is meaningless to a user ("what's a security rule?"),
     * and it's also the single most common failure mode in this app's whole party feature -- the
     * rules in `FIRESTORE_RULES.md` have to be manually re-pasted into the Firebase console every
     * time they change, a step outside this app's own reach, and it's easy to forget one existed.
     * Surfacing it as an actionable message instead of the raw exception text turns "I can't
     * remove a user, nothing happens" into something the user can actually act on.
     */
    private final java.lang.String friendlyFirestoreError(java.lang.Exception e, java.lang.String action) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createParty(@org.jetbrains.annotations.NotNull()
    java.lang.String hostUid, @org.jetbrains.annotations.NotNull()
    java.lang.String hostName, @org.jetbrains.annotations.NotNull()
    java.lang.String partyName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.CreatePartyResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object joinParty(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String uid, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.JoinPartyResult> $completion) {
        return null;
    }
    
    public final void leaveParty(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String uid) {
    }
    
    /**
     * Host-only (enforced by security rules): removes someone else's membership. The removed
     * member's own client detects this via [observeMembers] (their uid disappears from the live
     * list) and exits the party locally -- see `MainViewModel`'s membership-watch.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object kickMember(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String uid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.vibesync.app.data.party.PartyDoc> observeParty(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
        return null;
    }
    
    /**
     * Unordered on purpose -- ordering by two different fields depending on [QueueItemStatus]
     * would need a Firestore composite index; sorting the (small) result list client-side avoids
     * that entirely. Callers split/sort into pending-by-addedAt, approved-by-position, and
     * played-by-playedAt.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.party.PartyQueueItem>> observeQueue(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.party.PartyMember>> observeMembers(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
        return null;
    }
    
    /**
     * Any party member can request a song -- lands as "pending" until the host approves it.
     * [autoApprove] is host-only (enforced by security rules, not just this flag) and used when
     * the host plays something directly or queues up their own playlist.
     *
     * Refuses a song that's already pending or approved in this party (checked server-side via a
     * fresh query, not just against whatever the caller has cached, so two people adding the same
     * song at nearly the same moment still can't both get it in) -- a song that already PLAYED is
     * fine to re-request, since it's no longer "in the queue."
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addToQueue(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    java.lang.String addedByUid, @org.jetbrains.annotations.NotNull()
    java.lang.String addedByName, boolean autoApprove, long position, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Bulk version for "play this whole playlist" -- one batch write instead of N round trips.
     * Tracks already queued (pending/approved) are silently skipped rather than failing the
     * whole batch, so requesting a playlist that's 80% already-queued still adds the other 20%.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addPlaylistToQueue(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> tracks, @org.jetbrains.annotations.NotNull()
    java.lang.String addedByUid, @org.jetbrains.annotations.NotNull()
    java.lang.String addedByName, boolean autoApprove, long startPosition, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    private final java.lang.Object isAlreadyQueued(java.lang.String code, java.lang.String videoId, kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Host-only (enforced by security rules): approve a pending request, giving it a real
     * position at the end of the current approved queue.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object approveQueueItem(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String itemId, long position, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Only for a still-PENDING request the host doesn't want -- a real delete, unlike a PLAYED
     * item, which is kept around as history.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object rejectQueueItem(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: a real delete for a still-upcoming (APPROVED, not yet current) queue item --
     * distinct from [rejectQueueItem] (pending requests) and [markPlayed] (retiring the outgoing
     * track as history) since this is the host explicitly deciding "actually, not this one" for
     * something already sitting in "Up next".
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeQueueItem(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: rewrite every approved item's position in one batch, matching the drag-reorder
     * pattern already used for local playlists (see `PlaylistTrackDao.reorder()`).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object reorderQueue(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> orderedItemIds, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: marks a queue item PLAYED (with a server timestamp, so [playPrevious]-style
     * "most recently played" lookups are reliable) -- called on the outgoing track whenever the
     * party moves to a different one.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markPlayed(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: (re-)marks a queue item APPROVED at a given position -- used both for normal
     * approval and for "go back" pushing the previously-current item back into "Up next".
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markApproved(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String itemId, long position, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: sets what's "now playing" and freezes a fresh server timestamp as the sync
     * anchor -- see [com.vibesync.app.playback.PartySyncController] for how clients turn this
     * into an actual playback position, including for someone joining mid-song. [queueItemId] is
     * null for an ad-hoc play that was never added to the queue.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCurrentTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.Nullable()
    java.lang.String queueItemId, long startPositionMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only: play/pause/seek all funnel through here -- same write shape either way (freeze
     * the current position + a fresh server timestamp), since a seek is really just "pause and
     * immediately resume at a different position" from the sync formula's point of view.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setPlaybackState(@org.jetbrains.annotations.NotNull()
    java.lang.String code, boolean isPlaying, long positionMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.party.PartyActionResult> $completion) {
        return null;
    }
    
    private final java.lang.String randomCode() {
        return null;
    }
}