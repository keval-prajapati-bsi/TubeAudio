package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a&\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u00a8\u0006\f"}, d2 = {"InviteFriendsScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "Lkotlin/Function0;", "InviteRow", "friend", "Lcom/vibesync/app/data/social/Friend;", "alreadyInvited", "", "onInvite", "app_debug"})
public final class InviteFriendsScreenKt {
    
    /**
     * Host-only, reached from the party room's kebab menu -- invite one of your friends straight
     * into the party currently open, per item 7 of the request. Sending an invite doesn't move them
     * anywhere itself; they see it via their own Notifications page (in-app -- see
     * NotificationsScreen's doc comment) and choose whether to join.
     */
    @androidx.compose.runtime.Composable()
    public static final void InviteFriendsScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InviteRow(com.vibesync.app.data.social.Friend friend, boolean alreadyInvited, kotlin.jvm.functions.Function0<kotlin.Unit> onInvite) {
    }
}