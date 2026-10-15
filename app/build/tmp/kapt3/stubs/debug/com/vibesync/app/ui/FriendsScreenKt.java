package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a\u001e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u001a\u001e\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a&\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u001aF\u0010\u0013\u001a\u00020\u00012\b\u0010\u0014\u001a\u0004\u0018\u00010\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0016\u001a\u00020\u00112\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u00a8\u0006\u001a"}, d2 = {"Avatar", "", "name", "", "FriendRow", "friend", "Lcom/vibesync/app/data/social/Friend;", "onRemove", "Lkotlin/Function0;", "FriendsScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "SearchedUserRow", "profile", "Lcom/vibesync/app/data/social/UserProfile;", "alreadyFriend", "", "onAdd", "UsernameCard", "username", "error", "isSaving", "onSave", "Lkotlin/Function1;", "onDismissError", "app_debug"})
public final class FriendsScreenKt {
    
    /**
     * Reached via Profile -> Friends. Search-by-username is what item 6 of the request actually
     * asked for -- adding a friend is immediate and mutual (no request/approval step, unlike party
     * invites below, which do need one since accepting means joining someone's live party). Online/
     * offline is a heartbeat approximation, not real presence -- see
     * [com.vibesync.app.data.social.ONLINE_WINDOW_MILLIS]'s doc comment.
     */
    @androidx.compose.runtime.Composable()
    public static final void FriendsScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UsernameCard(java.lang.String username, java.lang.String error, boolean isSaving, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSave, kotlin.jvm.functions.Function0<kotlin.Unit> onDismissError) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchedUserRow(com.vibesync.app.data.social.UserProfile profile, boolean alreadyFriend, kotlin.jvm.functions.Function0<kotlin.Unit> onAdd) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FriendRow(com.vibesync.app.data.social.Friend friend, kotlin.jvm.functions.Function0<kotlin.Unit> onRemove) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Avatar(java.lang.String name) {
    }
}