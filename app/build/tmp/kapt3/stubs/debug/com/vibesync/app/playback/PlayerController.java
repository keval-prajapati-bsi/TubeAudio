package com.vibesync.app.playback;

/**
 * Application-scoped owner of the [MediaController] connection to
 * [PlaybackService], plus all queue/autoplay/prefetch/resume logic. Lives for
 * the whole process lifetime (see [com.vibesync.app.AppContainer]) so playback
 * and its UI state survive the host Activity/ViewModel being destroyed.
 *
 * Playback order is [queue] (the playlist's own order) filtered through
 * [playOrder] -- a permutation of `queue` indices that's either identity
 * (shuffle off) or shuffled with the currently-playing track pinned first
 * (shuffle on, so toggling shuffle never yanks you off the current song).
 * [orderPosition] is where we are within `playOrder`; the actual `queue`
 * index of "now playing" is `playOrder[orderPosition]`.
 *
 * The *next* track is eagerly resolved and appended to the real ExoPlayer
 * timeline (not just cached) so that `Player.hasNextMediaItem()` is genuinely
 * true -- that's what makes the system/lock-screen media notification show a
 * working "skip to next" button, since Media3's default notification derives
 * its buttons from the player's actually-available commands. "Previous" stays
 * a custom, on-demand operation (see [skipToPrevious]) since ExoPlayer's own
 * seek-to-previous already has sensible "restart current item" fallback
 * semantics even with nothing behind it in the timeline.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u009e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b \u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000e\u0010;\u001a\u00020<2\u0006\u0010=\u001a\u00020\u0013J\b\u0010>\u001a\u00020\u001fH\u0002J\b\u0010?\u001a\u00020<H\u0002J\u0010\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J,\u0010D\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00122\f\u0010E\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00122\u0006\u0010F\u001a\u00020\u001b2\u0006\u0010G\u001a\u00020\u001fH\u0002J\u0006\u0010H\u001a\u00020<J\b\u0010I\u001a\u00020<H\u0002J\u0010\u0010J\u001a\u00020<2\u0006\u0010K\u001a\u00020/H\u0002J\u000e\u0010L\u001a\u00020<2\u0006\u0010=\u001a\u00020\u0013J.\u0010M\u001a\u00020<2\u0006\u0010N\u001a\u00020/2\f\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010P\u001a\u00020\u001b2\b\b\u0002\u0010Q\u001a\u00020\u000eJ\b\u0010R\u001a\u00020<H\u0002J\u0014\u0010S\u001a\u00020<2\f\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012J\u0016\u0010U\u001a\u00020C2\u0006\u0010=\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010VJ\b\u0010W\u001a\u00020<H\u0002J\u000e\u0010X\u001a\u00020<2\u0006\u0010Y\u001a\u00020/J\u0010\u0010Z\u001a\u00020<2\b\u0010[\u001a\u0004\u0018\u00010\u0013J\u0006\u0010\\\u001a\u00020<J\u0006\u0010]\u001a\u00020<J\b\u0010^\u001a\u00020<H\u0002J\b\u0010_\u001a\u00020<H\u0002J\u0006\u0010`\u001a\u00020<J\u0006\u0010a\u001a\u00020<J\u0006\u0010b\u001a\u00020<R\u001a\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n \u0015*\u0004\u0018\u00010\u00030\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\u00020\u001b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u001a\u0010\u001e\u001a\u00020\u001fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010 \"\u0004\b!\u0010\"R\u000e\u0010#\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010*\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010.\u001a\u0004\u0018\u00010/X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u00100R\u001d\u00101\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r02\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u000e\u00105\u001a\u000206X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u00107\u001a\b\u0012\u0004\u0012\u00020\u001002\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00104R\u001d\u00109\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u001202\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u00104\u00a8\u0006c"}, d2 = {"Lcom/vibesync/app/playback/PlayerController;", "", "context", "Landroid/content/Context;", "libraryRepository", "Lcom/vibesync/app/data/LibraryRepository;", "playbackStateDao", "Lcom/vibesync/app/data/db/PlaybackStateDao;", "playbackHistoryPreferences", "Lcom/vibesync/app/data/PlaybackHistoryPreferences;", "(Landroid/content/Context;Lcom/vibesync/app/data/LibraryRepository;Lcom/vibesync/app/data/db/PlaybackStateDao;Lcom/vibesync/app/data/PlaybackHistoryPreferences;)V", "_queuedVideoIds", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "", "_uiState", "Lcom/vibesync/app/playback/PlaybackUiState;", "_upNext", "", "Lcom/vibesync/app/data/db/TrackEntity;", "appContext", "kotlin.jvm.PlatformType", "appendJob", "Lkotlinx/coroutines/Job;", "controller", "Landroidx/media3/session/MediaController;", "currentQueueIndex", "", "getCurrentQueueIndex", "()I", "isLockedToParty", "", "()Z", "setLockedToParty", "(Z)V", "isRepeatEnabled", "isShuffleEnabled", "orderPosition", "pendingNextChapters", "Lcom/vibesync/app/extractor/Chapter;", "pendingNextVideoId", "playJob", "playOrder", "playerListener", "Landroidx/media3/common/Player$Listener;", "queue", "queuePlaylistId", "", "Ljava/lang/Long;", "queuedVideoIds", "Lkotlinx/coroutines/flow/StateFlow;", "getQueuedVideoIds", "()Lkotlinx/coroutines/flow/StateFlow;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "uiState", "getUiState", "upNext", "getUpNext", "addToQueue", "", "track", "advanceOrderPosition", "appendNextToTimeline", "buildMediaItem", "Landroidx/media3/common/MediaItem;", "resolved", "Lcom/vibesync/app/extractor/ResolvedAudio;", "buildPlayOrder", "indices", "pinnedFirst", "shuffle", "dismissError", "persistPlaybackState", "playCurrentAtOrderPosition", "seekPositionMs", "playFromUpNext", "playQueue", "playlistId", "tracks", "startIndex", "label", "refreshUpNext", "reorderUpNext", "newUpNextOrder", "resolveTrack", "(Lcom/vibesync/app/data/db/TrackEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "restoreLastPlayed", "seekTo", "positionMs", "setPartyUpcoming", "next", "skipToNext", "skipToPrevious", "startPositionTicker", "syncDuration", "togglePlayPause", "toggleRepeat", "toggleShuffle", "app_debug"})
public final class PlayerController {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.LibraryRepository libraryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.PlaybackStateDao playbackStateDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.PlaybackHistoryPreferences playbackHistoryPreferences = null;
    private final android.content.Context appContext = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.session.MediaController controller;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.vibesync.app.data.db.TrackEntity> queue;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.Integer> playOrder;
    private int orderPosition = -1;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long queuePlaylistId;
    private boolean isShuffleEnabled = false;
    private boolean isRepeatEnabled = false;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job playJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job appendJob;
    
    /**
     * The next-in-queue track's chapters, fetched during [appendNextToTimeline]'s resolve but not
     * needed until [onMediaItemTransition] actually fires for it (a natural auto-advance) --
     * cached here rather than re-resolving or dropping them on the floor.
     */
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingNextVideoId;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.vibesync.app.extractor.Chapter> pendingNextChapters;
    
    /**
     * Set by [com.vibesync.app.ui.MainViewModel] while the user is in a party as a non-host
     * member -- blocks [playQueue]/[addToQueue] from loading anything other than the party's own
     * synced track ([com.vibesync.app.playback.PARTY_QUEUE_ID]), so browsing Search/Library/etc.
     * while in someone else's party can't hijack local playback away from what the party is
     * actually playing. The host is deliberately NOT locked -- they have their own party-aware
     * controls (see `MainViewModel.togglePartyPlayPause`/skip/etc.) that keep the shared party
     * state in sync; a non-host has no such path, so blocking is the only way to prevent drift.
     */
    private boolean isLockedToParty = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.vibesync.app.playback.PlaybackUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.PlaybackUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> _upNext = null;
    
    /**
     * Everything after the currently-playing track in [playOrder], for the "Up Next"/Queue
     * screen -- recomputed on every queue/shuffle/repeat/track-advance mutation below.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> upNext = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Set<java.lang.String>> _queuedVideoIds = null;
    
    /**
     * Every videoId currently anywhere in [queue] (already playing or upcoming) -- lets a
     * listing row show "already in queue" instead of offering to add it again. Recomputed
     * alongside [_upNext] at every mutation point below.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> queuedVideoIds = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.media3.common.Player.Listener playerListener = null;
    
    public PlayerController(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.LibraryRepository libraryRepository, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaybackStateDao playbackStateDao, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.PlaybackHistoryPreferences playbackHistoryPreferences) {
        super();
    }
    
    /**
     * Set by [com.vibesync.app.ui.MainViewModel] while the user is in a party as a non-host
     * member -- blocks [playQueue]/[addToQueue] from loading anything other than the party's own
     * synced track ([com.vibesync.app.playback.PARTY_QUEUE_ID]), so browsing Search/Library/etc.
     * while in someone else's party can't hijack local playback away from what the party is
     * actually playing. The host is deliberately NOT locked -- they have their own party-aware
     * controls (see `MainViewModel.togglePartyPlayPause`/skip/etc.) that keep the shared party
     * state in sync; a non-host has no such path, so blocking is the only way to prevent drift.
     */
    public final boolean isLockedToParty() {
        return false;
    }
    
    /**
     * Set by [com.vibesync.app.ui.MainViewModel] while the user is in a party as a non-host
     * member -- blocks [playQueue]/[addToQueue] from loading anything other than the party's own
     * synced track ([com.vibesync.app.playback.PARTY_QUEUE_ID]), so browsing Search/Library/etc.
     * while in someone else's party can't hijack local playback away from what the party is
     * actually playing. The host is deliberately NOT locked -- they have their own party-aware
     * controls (see `MainViewModel.togglePartyPlayPause`/skip/etc.) that keep the shared party
     * state in sync; a non-host has no such path, so blocking is the only way to prevent drift.
     */
    public final void setLockedToParty(boolean p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.vibesync.app.playback.PlaybackUiState> getUiState() {
        return null;
    }
    
    /**
     * Everything after the currently-playing track in [playOrder], for the "Up Next"/Queue
     * screen -- recomputed on every queue/shuffle/repeat/track-advance mutation below.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.vibesync.app.data.db.TrackEntity>> getUpNext() {
        return null;
    }
    
    /**
     * Every videoId currently anywhere in [queue] (already playing or upcoming) -- lets a
     * listing row show "already in queue" instead of offering to add it again. Recomputed
     * alongside [_upNext] at every mutation point below.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.lang.String>> getQueuedVideoIds() {
        return null;
    }
    
    private final int getCurrentQueueIndex() {
        return 0;
    }
    
    private final void refreshUpNext() {
    }
    
    /**
     * Starts playing [tracks] (in order) beginning at [startIndex], remembering [playlistId]
     * as the autoplay/skip context. [label] is shown as "Playing from {label}" on the expanded
     * Now Playing screen.
     */
    public final void playQueue(long playlistId, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> tracks, int startIndex, @org.jetbrains.annotations.NotNull()
    java.lang.String label) {
    }
    
    /**
     * Jumps straight to a track already sitting in [upNext] (tapped from the Queue screen)
     * without re-resolving the whole queue.
     */
    public final void playFromUpNext(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track) {
    }
    
    /**
     * Applies a manually drag-reordered "Up Next" list from the Queue screen -- replaces
     * everything in [playOrder] after the currently-playing position, leaving playback itself
     * untouched. The next track may have changed, so this also drops whatever ExoPlayer already
     * had queued past the current item (it was appended eagerly under the old order) and
     * re-appends fresh via [appendNextToTimeline].
     */
    public final void reorderUpNext(@org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> newUpNextOrder) {
    }
    
    /**
     * Appends [track] to the very end of the current queue (a listing row's "Add to queue"
     * action) -- if nothing is currently playing, there's nothing to queue behind, so this just
     * starts playing it instead.
     */
    public final void addToQueue(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track) {
    }
    
    /**
     * Party mode only: keeps a live 2-item ExoPlayer timeline (currently-playing + the party
     * queue's next approved item) so a natural end-of-track transition plays already-buffered
     * audio immediately, the same native gapless path every other queue in this app already gets
     * via [appendNextToTimeline] -- party playback was previously always a "queue of one"
     * ([queue] holding just the current track), so [appendNextToTimeline] never had anything to
     * append and every party track transition hard-stopped, waited on [PartySyncController]'s
     * poll, then re-resolved the next stream from scratch over the network. That round trip was
     * the actual cause of the audible gap between songs reported in party sessions. Safe to call
     * repeatedly as the party's queue changes; a no-op if [next] is already what's queued up, and
     * a no-op entirely outside party playback.
     */
    public final void setPartyUpcoming(@org.jetbrains.annotations.Nullable()
    com.vibesync.app.data.db.TrackEntity next) {
    }
    
    public final void togglePlayPause() {
    }
    
    public final void toggleShuffle() {
    }
    
    public final void toggleRepeat() {
    }
    
    /**
     * Prefers a genuinely-native skip (instant, works from the lock screen too) when the
     * next item has already been appended; falls back to a fresh resolve otherwise.
     */
    public final void skipToNext() {
    }
    
    public final void skipToPrevious() {
    }
    
    public final void seekTo(long positionMs) {
    }
    
    public final void dismissError() {
    }
    
    /**
     * Moves [orderPosition] to the next entry, wrapping to the start if repeat is on.
     * Returns false (and leaves position untouched) if there's nowhere to go.
     */
    private final boolean advanceOrderPosition() {
        return false;
    }
    
    private final java.util.List<java.lang.Integer> buildPlayOrder(java.util.List<java.lang.Integer> indices, int pinnedFirst, boolean shuffle) {
        return null;
    }
    
    private final void playCurrentAtOrderPosition(long seekPositionMs) {
    }
    
    /**
     * Resolves the upcoming track (following [playOrder], wrapping if repeat is on) and
     * appends it to the live ExoPlayer timeline (rather than just caching it) so native
     * seek-to-next -- including from the system notification -- has something to jump to.
     */
    private final void appendNextToTimeline() {
    }
    
    /**
     * Downloaded tracks play straight from disk (also works fully offline); everything
     * else is resolved fresh over the network since stream URLs are short-lived.
     * [TrackEntity.localFilePath] is either a plain filesystem path (app-private storage, the
     * default) or a `content://` URI string (a custom download location the user picked via
     * Storage Access Framework, see [com.vibesync.app.data.DownloadLocationPreferences]) --
     * Media3's `DefaultDataSource` already handles both `file://` and `content://` schemes
     * natively, so no special-casing is needed on the playback side beyond building the right
     * URI and checking existence the right way for each.
     */
    private final java.lang.Object resolveTrack(com.vibesync.app.data.db.TrackEntity track, kotlin.coroutines.Continuation<? super com.vibesync.app.extractor.ResolvedAudio> $completion) {
        return null;
    }
    
    private final androidx.media3.common.MediaItem buildMediaItem(com.vibesync.app.extractor.ResolvedAudio resolved) {
        return null;
    }
    
    private final void restoreLastPlayed() {
    }
    
    private final void persistPlaybackState() {
    }
    
    private final void syncDuration() {
    }
    
    private final void startPositionTicker() {
    }
}