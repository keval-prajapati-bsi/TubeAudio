package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000R\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\b\u0010\b\u001a\u00020\u0001H\u0003\u001a&\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a(\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\r2\u000e\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0007H\u0003\u001a|\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\r2\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\u001e\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001al\u0010 \u001a\u00020\u00012\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010\"2\u0006\u0010$\u001a\u00020\r2\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020&2\u0006\u0010\u0016\u001a\u00020\r2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\u00010+H\u0003\u001a\u0010\u0010,\u001a\u00020\u00012\u0006\u0010-\u001a\u00020\"H\u0003\u00a8\u0006."}, d2 = {"AddSongToLocalRoomScreen", "", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "room", "Lcom/vibesync/app/data/localparty/LocalRoomState;", "onBack", "Lkotlin/Function0;", "ConnectingLocalRoomScreen", "LocalAddSongResultRow", "result", "Lcom/vibesync/app/extractor/SearchResult;", "alreadyQueued", "", "onClick", "LocalMemberChip", "member", "Lcom/vibesync/app/data/localparty/LocalMember;", "isMe", "LocalQueueRow", "item", "Lcom/vibesync/app/data/localparty/LocalQueueItem;", "isHost", "canMoveUp", "canMoveDown", "onPlayNow", "onMoveUp", "onMoveDown", "onMoveToTop", "onMoveToBottom", "onRemove", "LocalRoomScreen", "NowPlayingLocalCard", "title", "", "thumbnailUrl", "isPlaying", "positionMs", "", "durationMs", "onPlayPause", "onNext", "onSeek", "Lkotlin/Function1;", "SectionLabel", "text", "app_debug"})
public final class LocalRoomScreenKt {
    
    /**
     * The active local room: mirrors PartyRoomScreen's structure closely, adapted for
     * [com.vibesync.app.data.localparty.LocalRoomState]'s flatter model (no pending/approve queue --
     * see that class's doc comment). Closing this screen (back) doesn't leave the room, same "stays
     * running in the background, reachable via Menu" pattern as the Firebase Party Room.
     */
    @androidx.compose.runtime.Composable()
    public static final void LocalRoomScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ConnectingLocalRoomScreen() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionLabel(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LocalMemberChip(com.vibesync.app.data.localparty.LocalMember member, boolean isMe, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void NowPlayingLocalCard(java.lang.String title, java.lang.String thumbnailUrl, boolean isPlaying, long positionMs, long durationMs, boolean isHost, kotlin.jvm.functions.Function0<kotlin.Unit> onPlayPause, kotlin.jvm.functions.Function0<kotlin.Unit> onNext, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onSeek) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LocalQueueRow(com.vibesync.app.data.localparty.LocalQueueItem item, boolean isHost, boolean canMoveUp, boolean canMoveDown, kotlin.jvm.functions.Function0<kotlin.Unit> onPlayNow, kotlin.jvm.functions.Function0<kotlin.Unit> onMoveUp, kotlin.jvm.functions.Function0<kotlin.Unit> onMoveDown, kotlin.jvm.functions.Function0<kotlin.Unit> onMoveToTop, kotlin.jvm.functions.Function0<kotlin.Unit> onMoveToBottom, kotlin.jvm.functions.Function0<kotlin.Unit> onRemove) {
    }
    
    /**
     * Search YouTube and request a song for the local room -- same "full screen, not a dialog"
     * pattern as the Firebase party's add-song screen (see PartyRoomScreen.kt's AddSongToPartyScreen
     * doc comment for why), reusing that same search state/debounce plumbing
     * (`MainViewModel.partyAddQuery` etc.) since it's generic YouTube search unrelated to which room
     * type is active -- the two features are mutually exclusive at any given moment anyway.
     */
    @androidx.compose.runtime.Composable()
    private static final void AddSongToLocalRoomScreen(com.vibesync.app.ui.MainViewModel viewModel, com.vibesync.app.data.localparty.LocalRoomState room, kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LocalAddSongResultRow(com.vibesync.app.extractor.SearchResult result, boolean alreadyQueued, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}