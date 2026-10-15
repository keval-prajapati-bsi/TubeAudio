package com.vibesync.app.data.social;

/**
 * Firestore-backed friends/usernames/party-invites, all client + security-rules only (no backend,
 * same architecture as [com.vibesync.app.data.party.PartyRepository]). See `FIRESTORE_RULES.md`
 * for the rules this data model assumes.
 *
 * - `usernames/{usernameLower}` is a reservation doc (`{uid}`) used purely to make usernames
 *  unique -- created/deleted inside the same transaction as `users/{uid}.username` so the two
 *  never disagree about who currently owns a name.
 * - `users/{uid}/friends/{friendUid}` -- each side of a friendship writes its own copy. Adding a
 *  friend is a single client-side batch (both docs at once, no approval step) made possible by a
 *  security-rule trick: you're always allowed to write *yourself* into someone else's friends
 *  subcollection (never an arbitrary other uid), so the reciprocal write doesn't need the other
 *  person's client to be the one performing it.
 * - `partyInvites/{id}` -- a real accept/reject flow (unlike adding a friend), since accepting one
 *  means actually joining someone's live party.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J0\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u001c\u0010\u0012\u001a\u00020\f2\n\u0010\u0013\u001a\u00060\u0014j\u0002`\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0002J\u0010\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\fH\u0002J\u001a\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u001b0\u001a2\u0006\u0010\u0018\u001a\u00020\fJ\u001a\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u001b0\u001a2\u0006\u0010\u0018\u001a\u00020\fJ\u0016\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u001a2\u0006\u0010\u0018\u001a\u00020\fJ\u001e\u0010 \u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010!\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\"J\u001e\u0010#\u001a\u00020\n2\u0006\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020&H\u0086@\u00a2\u0006\u0002\u0010\'J\u001c\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00100\u001b2\u0006\u0010)\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010*J\u0016\u0010+\u001a\u00020,2\u0006\u0010\u0018\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010*J6\u0010-\u001a\u00020\n2\u0006\u0010.\u001a\u00020\f2\u0006\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\f2\u0006\u00101\u001a\u00020\f2\u0006\u00102\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u00103J\u001e\u00104\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\f2\u0006\u00105\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\"R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/vibesync/app/data/social/SocialRepository;", "", "()V", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "invitesRef", "Lcom/google/firebase/firestore/CollectionReference;", "usernamesRef", "usersRef", "addFriend", "Lcom/vibesync/app/data/social/SocialActionResult;", "myUid", "", "myDisplayName", "myUsername", "friend", "Lcom/vibesync/app/data/social/UserProfile;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/vibesync/app/data/social/UserProfile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "friendlyFirestoreError", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "action", "friendsRef", "uid", "observeFriends", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/vibesync/app/data/social/Friend;", "observeMyPendingInvites", "Lcom/vibesync/app/data/social/PartyInvite;", "observeMyProfile", "removeFriend", "friendUid", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "respondToInvite", "inviteId", "accept", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchUsersByUsername", "prefix", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendHeartbeat", "", "sendPartyInvite", "fromUid", "fromDisplayName", "toUid", "partyCode", "partyName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setUsername", "desired", "app_debug"})
public final class SocialRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.CollectionReference usersRef = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.CollectionReference usernamesRef = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.CollectionReference invitesRef = null;
    
    public SocialRepository() {
        super();
    }
    
    private final com.google.firebase.firestore.CollectionReference friendsRef(java.lang.String uid) {
        return null;
    }
    
    private final java.lang.String friendlyFirestoreError(java.lang.Exception e, java.lang.String action) {
        return null;
    }
    
    /**
     * Claims [desired] as this user's unique, case-insensitively-reserved username, freeing
     * whatever username they held before (if any). Fails with a plain "already taken" message
     * (not a raw Firestore error) if someone else holds it -- checked and claimed atomically via
     * a transaction, so two people racing for the same name can't both win.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setUsername(@org.jetbrains.annotations.NotNull()
    java.lang.String uid, @org.jetbrains.annotations.NotNull()
    java.lang.String desired, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.social.SocialActionResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.vibesync.app.data.social.UserProfile> observeMyProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String uid) {
        return null;
    }
    
    /**
     * Best-effort, not real presence -- see [ONLINE_WINDOW_MILLIS]'s doc comment. Called on a
     * timer from [com.vibesync.app.ui.MainViewModel] while the app is in the foreground.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendHeartbeat(@org.jetbrains.annotations.NotNull()
    java.lang.String uid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Prefix search on the (lowercased) username -- a single-field range query, so no manual
     * Firestore composite index is needed, same "keep setup to just the one rules paste"
     * constraint the rest of this app's Firestore usage follows. Case-insensitive by construction
     * since [usernameLower] is what's indexed against.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchUsersByUsername(@org.jetbrains.annotations.NotNull()
    java.lang.String prefix, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.social.UserProfile>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.social.Friend>> observeFriends(@org.jetbrains.annotations.NotNull()
    java.lang.String uid) {
        return null;
    }
    
    /**
     * Immediate, mutual, no approval step -- both `users/{me}/friends/{them}` and
     * `users/{them}/friends/{me}` are written in one batch. See the class doc comment for the
     * security-rule trick that makes the second (writing into *their* subcollection) legal.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addFriend(@org.jetbrains.annotations.NotNull()
    java.lang.String myUid, @org.jetbrains.annotations.NotNull()
    java.lang.String myDisplayName, @org.jetbrains.annotations.Nullable()
    java.lang.String myUsername, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.social.UserProfile friend, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.social.SocialActionResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeFriend(@org.jetbrains.annotations.NotNull()
    java.lang.String myUid, @org.jetbrains.annotations.NotNull()
    java.lang.String friendUid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.social.SocialActionResult> $completion) {
        return null;
    }
    
    /**
     * Host-only (enforced by security rules, which check the party doc's own `hostUid`). Refuses
     * a duplicate still-pending invite to the same friend for the same party -- three equality
     * filters, still no composite index needed.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendPartyInvite(@org.jetbrains.annotations.NotNull()
    java.lang.String fromUid, @org.jetbrains.annotations.NotNull()
    java.lang.String fromDisplayName, @org.jetbrains.annotations.NotNull()
    java.lang.String toUid, @org.jetbrains.annotations.NotNull()
    java.lang.String partyCode, @org.jetbrains.annotations.NotNull()
    java.lang.String partyName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.social.SocialActionResult> $completion) {
        return null;
    }
    
    /**
     * In-app only, by design (see version25/CHANGES.txt) -- this only reflects new invites while
     * the app is actually running (foreground or briefly backgrounded), not a true push
     * notification delivered while the app is fully closed. Real push would need Firebase Cloud
     * Messaging plus a server-side trigger (a Cloud Function), a real infrastructure/billing
     * change this app doesn't have and wasn't asked for yet.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.social.PartyInvite>> observeMyPendingInvites(@org.jetbrains.annotations.NotNull()
    java.lang.String uid) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object respondToInvite(@org.jetbrains.annotations.NotNull()
    java.lang.String inviteId, boolean accept, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.social.SocialActionResult> $completion) {
        return null;
    }
}