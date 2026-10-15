package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u001e\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u00a8\u0006\u000b"}, d2 = {"InviteRow", "", "invite", "Lcom/vibesync/app/data/social/PartyInvite;", "onAccept", "Lkotlin/Function0;", "onReject", "NotificationsScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "app_debug"})
public final class NotificationsScreenKt {
    
    /**
     * Pending party invites from friends -- reached from Profile's badge-counted "Party invites" row.
     * This is the "notification page" from item 7 of the request; it's deliberately in-app-only
     * (no system notification while the app is fully closed), see
     * [com.vibesync.app.data.social.SocialRepository.observeMyPendingInvites]'s doc comment for the
     * disclosed reason (no backend, no Firebase Cloud Messaging trigger).
     */
    @androidx.compose.runtime.Composable()
    public static final void NotificationsScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InviteRow(com.vibesync.app.data.social.PartyInvite invite, kotlin.jvm.functions.Function0<kotlin.Unit> onAccept, kotlin.jvm.functions.Function0<kotlin.Unit> onReject) {
    }
}