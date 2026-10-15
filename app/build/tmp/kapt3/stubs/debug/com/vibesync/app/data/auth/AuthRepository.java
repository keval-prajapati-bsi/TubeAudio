package com.vibesync.app.data.auth;

/**
 * Plain email/password accounts via Firebase Auth -- Google Sign-In was dropped for now (needs
 * an OAuth consent screen + auto-provisioned web client that wasn't coming through cleanly), but
 * everything here is keyed by [FirebaseAuth]'s uid, so adding another sign-in provider later
 * doesn't require touching the party/queue data model at all. Every signed-up user also gets a
 * `users/{uid}` Firestore doc (displayName/email/createdAt) -- this is the "users table" the
 * party feature's member lists and host/requester attribution read from.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0006\u0010\u0018\u001a\u00020\u0019J&\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u0010\u0010\u001d\u001a\u00020\u0006*\u00060\u001ej\u0002`\u001fH\u0002J\f\u0010 \u001a\u00020\u000b*\u00020!H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0019\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/vibesync/app/data/auth/AuthRepository;", "", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "currentUid", "", "getCurrentUid", "()Ljava/lang/String;", "currentUserFlow", "Lkotlinx/coroutines/flow/Flow;", "Lcom/vibesync/app/data/auth/AppUser;", "getCurrentUserFlow", "()Lkotlinx/coroutines/flow/Flow;", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "signIn", "Lcom/vibesync/app/data/auth/AuthResult;", "email", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signInWithGoogle", "idToken", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signOut", "", "signUp", "displayName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "friendlyMessage", "Ljava/lang/Exception;", "Lkotlin/Exception;", "toAppUser", "Lcom/google/firebase/auth/FirebaseUser;", "app_debug"})
public final class AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    
    /**
     * Emits the signed-in user (or null) immediately and on every sign-in/out change --
     * Firebase Auth persists the session locally, so this reflects a cached signed-in user
     * before any network round-trip completes on app relaunch.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.vibesync.app.data.auth.AppUser> currentUserFlow = null;
    
    public AuthRepository() {
        super();
    }
    
    /**
     * Emits the signed-in user (or null) immediately and on every sign-in/out change --
     * Firebase Auth persists the session locally, so this reflects a cached signed-in user
     * before any network round-trip completes on app relaunch.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.vibesync.app.data.auth.AppUser> getCurrentUserFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUid() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signUp(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.auth.AuthResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signIn(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.auth.AuthResult> $completion) {
        return null;
    }
    
    /**
     * [idToken] comes from Google's own sign-in Intent (see AuthScreen's launcher) -- exchanged
     * here for a real Firebase session. Also (best-effort) upserts the `users/{uid}` doc, since a
     * first-time Google sign-in never goes through [signUp].
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signInWithGoogle(@org.jetbrains.annotations.NotNull()
    java.lang.String idToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.auth.AuthResult> $completion) {
        return null;
    }
    
    public final void signOut() {
    }
    
    private final com.vibesync.app.data.auth.AppUser toAppUser(com.google.firebase.auth.FirebaseUser $this$toAppUser) {
        return null;
    }
    
    /**
     * Firebase's raw exception messages are technical ("The email address is badly formatted.")
     * -- these are already user-presentable as-is, but network failures throw a generic
     * [java.io.IOException] with no message at all, so give that case an actual sentence.
     */
    private final java.lang.String friendlyMessage(java.lang.Exception $this$friendlyMessage) {
        return null;
    }
}