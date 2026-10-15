package com.vibesync.app.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.vibesync.app.data.ADHOC_QUEUE_ID
import com.vibesync.app.data.DOWNLOADS_QUEUE_ID
import com.vibesync.app.data.LIKED_QUEUE_ID
import com.vibesync.app.data.LibraryRepository
import com.vibesync.app.data.PlaybackHistoryPreferences
import com.vibesync.app.data.db.PlaybackStateDao
import com.vibesync.app.data.db.PlaybackStateEntity
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.extractor.AudioResolutionException
import com.vibesync.app.extractor.AudioStreamResolver
import com.vibesync.app.extractor.Chapter
import com.vibesync.app.extractor.ResolvedAudio
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val RESTART_INSTEAD_OF_PREVIOUS_THRESHOLD_MS = 3_000L
private const val STATE_SAVE_INTERVAL_TICKS = 10 // ticker runs every 500ms -> save every ~5s

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
class PlayerController(
    context: Context,
    private val libraryRepository: LibraryRepository,
    private val playbackStateDao: PlaybackStateDao,
    private val playbackHistoryPreferences: PlaybackHistoryPreferences
) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var controller: MediaController? = null

    private var queue: List<TrackEntity> = emptyList()
    private var playOrder: List<Int> = emptyList()
    private var orderPosition: Int = -1
    private var queuePlaylistId: Long? = null
    private var isShuffleEnabled: Boolean = false
    private var isRepeatEnabled: Boolean = false

    private var playJob: Job? = null
    private var appendJob: Job? = null

    /** The next-in-queue track's chapters, fetched during [appendNextToTimeline]'s resolve but not
     *  needed until [onMediaItemTransition] actually fires for it (a natural auto-advance) --
     *  cached here rather than re-resolving or dropping them on the floor. */
    private var pendingNextVideoId: String? = null
    private var pendingNextChapters: List<Chapter> = emptyList()

    /** Set by [com.vibesync.app.ui.MainViewModel] while the user is in a party as a non-host
     *  member -- blocks [playQueue]/[addToQueue] from loading anything other than the party's own
     *  synced track ([com.vibesync.app.playback.PARTY_QUEUE_ID]), so browsing Search/Library/etc.
     *  while in someone else's party can't hijack local playback away from what the party is
     *  actually playing. The host is deliberately NOT locked -- they have their own party-aware
     *  controls (see `MainViewModel.togglePartyPlayPause`/skip/etc.) that keep the shared party
     *  state in sync; a non-host has no such path, so blocking is the only way to prevent drift. */
    var isLockedToParty: Boolean = false

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    private val _upNext = MutableStateFlow<List<TrackEntity>>(emptyList())
    /** Everything after the currently-playing track in [playOrder], for the "Up Next"/Queue
     *  screen -- recomputed on every queue/shuffle/repeat/track-advance mutation below. */
    val upNext: StateFlow<List<TrackEntity>> = _upNext.asStateFlow()

    private val _queuedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    /** Every videoId currently anywhere in [queue] (already playing or upcoming) -- lets a
     *  listing row show "already in queue" instead of offering to add it again. Recomputed
     *  alongside [_upNext] at every mutation point below. */
    val queuedVideoIds: StateFlow<Set<String>> = _queuedVideoIds.asStateFlow()

    private val currentQueueIndex: Int
        get() = playOrder.getOrElse(orderPosition) { -1 }

    private fun refreshUpNext() {
        _upNext.value = playOrder.drop(orderPosition + 1).mapNotNull { queue.getOrNull(it) }
        _queuedVideoIds.value = queue.mapTo(mutableSetOf()) { it.videoId }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            if (!isPlaying) persistPlaybackState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.value = _uiState.value.copy(isBuffering = playbackState == Player.STATE_BUFFERING)
            if (playbackState == Player.STATE_READY) syncDuration()
            if (playbackState == Player.STATE_ENDED) {
                // Safety net: if the next item didn't get appended in time (or at
                // all), don't let autoplay silently stop -- fall back to a manual
                // advance exactly like before this native-queue rework existed.
                if (controller?.hasNextMediaItem() != true && advanceOrderPosition()) {
                    playCurrentAtOrderPosition(seekPositionMs = 0L)
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val videoId = mediaItem?.mediaId ?: return
            if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && reason != Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) return
            val newOrderPos = playOrder.indexOfFirst { queue.getOrNull(it)?.videoId == videoId }
            if (newOrderPos < 0 || newOrderPos == orderPosition) return

            // The player itself already advanced (natural end-of-track, or a
            // native seekToNext from our UI or the system notification) -- catch
            // up our own bookkeeping so autoplay/persistence/the next-next
            // prefetch all stay correct regardless of who triggered the move.
            orderPosition = newOrderPos
            val track = queue[currentQueueIndex]
            // The next track's chapters were already fetched during appendNextToTimeline()'s
            // resolve, just never surfaced anywhere -- cached there specifically so a natural
            // auto-advance doesn't have to re-resolve (or silently show no chapters) here.
            val chapters = if (pendingNextVideoId == track.videoId) pendingNextChapters else emptyList()
            pendingNextVideoId = null
            pendingNextChapters = emptyList()
            _uiState.value = _uiState.value.copy(
                isBuffering = true,
                videoId = track.videoId,
                title = track.title,
                thumbnailUrl = track.thumbnailUrl,
                url = track.url,
                positionMs = 0L,
                durationMs = track.durationSeconds * 1000L,
                hasNext = false,
                errorMessage = null,
                chapters = chapters
            )
            refreshUpNext()
            playbackHistoryPreferences.recordPlay(track)
            persistPlaybackState()
            appendNextToTimeline()
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Playback error: ${error.message ?: "stream failed"}",
                isBuffering = false
            )
        }
    }

    init {
        val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture.addListener({
            val connected = controllerFuture.get()
            connected.addListener(playerListener)
            controller = connected
            _uiState.value = _uiState.value.copy(isPlaying = connected.isPlaying)
        }, MoreExecutors.directExecutor())

        restoreLastPlayed()
        startPositionTicker()
    }

    /** Starts playing [tracks] (in order) beginning at [startIndex], remembering [playlistId]
     *  as the autoplay/skip context. [label] is shown as "Playing from {label}" on the expanded
     *  Now Playing screen. */
    fun playQueue(playlistId: Long, tracks: List<TrackEntity>, startIndex: Int, label: String = "") {
        if (tracks.isEmpty()) return
        if (isLockedToParty && playlistId != PARTY_QUEUE_ID) {
            _uiState.value = _uiState.value.copy(errorMessage = "You're in a party -- only the host controls what plays.")
            return
        }
        queue = tracks
        val pinned = startIndex.coerceIn(0, tracks.lastIndex)
        playOrder = buildPlayOrder(tracks.indices.toList(), pinnedFirst = pinned, shuffle = isShuffleEnabled)
        orderPosition = playOrder.indexOf(pinned).coerceAtLeast(0)
        queuePlaylistId = playlistId
        _uiState.value = _uiState.value.copy(queueLabel = label)
        playCurrentAtOrderPosition(seekPositionMs = 0L)
    }

    /** Jumps straight to a track already sitting in [upNext] (tapped from the Queue screen)
     *  without re-resolving the whole queue. */
    fun playFromUpNext(track: TrackEntity) {
        val targetOrderPos = playOrder.indexOfFirst { queue.getOrNull(it)?.videoId == track.videoId }
        if (targetOrderPos < 0) return
        orderPosition = targetOrderPos
        playCurrentAtOrderPosition(seekPositionMs = 0L)
    }

    /** Applies a manually drag-reordered "Up Next" list from the Queue screen -- replaces
     *  everything in [playOrder] after the currently-playing position, leaving playback itself
     *  untouched. The next track may have changed, so this also drops whatever ExoPlayer already
     *  had queued past the current item (it was appended eagerly under the old order) and
     *  re-appends fresh via [appendNextToTimeline]. */
    fun reorderUpNext(newUpNextOrder: List<TrackEntity>) {
        val remainingQueueIndices = newUpNextOrder.mapNotNull { track ->
            queue.indexOfFirst { it.videoId == track.videoId }.takeIf { it >= 0 }
        }
        playOrder = playOrder.take(orderPosition + 1) + remainingQueueIndices
        controller?.let { c -> while (c.mediaItemCount > 1) c.removeMediaItem(1) }
        _uiState.value = _uiState.value.copy(hasNext = false)
        refreshUpNext()
        appendNextToTimeline()
    }

    /** Appends [track] to the very end of the current queue (a listing row's "Add to queue"
     *  action) -- if nothing is currently playing, there's nothing to queue behind, so this just
     *  starts playing it instead. */
    fun addToQueue(track: TrackEntity) {
        if (isLockedToParty) {
            _uiState.value = _uiState.value.copy(errorMessage = "You're in a party -- only the host controls what plays.")
            return
        }
        if (queue.isEmpty()) {
            playQueue(ADHOC_QUEUE_ID, listOf(track), 0, "Queue")
            return
        }
        val wasAtTail = orderPosition == playOrder.lastIndex
        queue = queue + track
        playOrder = playOrder + queue.lastIndex
        refreshUpNext()
        if (wasAtTail) appendNextToTimeline()
    }

    /** Party mode only: keeps a live 2-item ExoPlayer timeline (currently-playing + the party
     *  queue's next approved item) so a natural end-of-track transition plays already-buffered
     *  audio immediately, the same native gapless path every other queue in this app already gets
     *  via [appendNextToTimeline] -- party playback was previously always a "queue of one"
     *  ([queue] holding just the current track), so [appendNextToTimeline] never had anything to
     *  append and every party track transition hard-stopped, waited on [PartySyncController]'s
     *  poll, then re-resolved the next stream from scratch over the network. That round trip was
     *  the actual cause of the audible gap between songs reported in party sessions. Safe to call
     *  repeatedly as the party's queue changes; a no-op if [next] is already what's queued up, and
     *  a no-op entirely outside party playback. */
    fun setPartyUpcoming(next: TrackEntity?) {
        if (queuePlaylistId != PARTY_QUEUE_ID || queue.isEmpty() || orderPosition < 0) return
        val current = queue.getOrNull(currentQueueIndex) ?: return
        val existingNext = playOrder.getOrNull(orderPosition + 1)?.let { queue.getOrNull(it) }
        if (existingNext?.videoId == next?.videoId) return
        queue = listOfNotNull(current, next)
        playOrder = queue.indices.toList()
        orderPosition = 0
        controller?.let { c -> while (c.mediaItemCount > 1) c.removeMediaItem(1) }
        refreshUpNext()
        if (next != null) appendNextToTimeline() else _uiState.value = _uiState.value.copy(hasNext = false)
    }

    fun togglePlayPause() {
        if (_uiState.value.needsResolveToResume) {
            playCurrentAtOrderPosition(seekPositionMs = _uiState.value.positionMs)
            return
        }
        val current = controller ?: return
        if (current.isPlaying) current.pause() else current.play()
    }

    fun toggleShuffle() {
        val current = currentQueueIndex
        isShuffleEnabled = !isShuffleEnabled
        if (queue.isNotEmpty() && current >= 0) {
            playOrder = buildPlayOrder(queue.indices.toList(), pinnedFirst = current, shuffle = isShuffleEnabled)
            orderPosition = playOrder.indexOf(current).coerceAtLeast(0)
        }
        _uiState.value = _uiState.value.copy(isShuffleEnabled = isShuffleEnabled)
        refreshUpNext()
        appendNextToTimeline()
    }

    fun toggleRepeat() {
        isRepeatEnabled = !isRepeatEnabled
        _uiState.value = _uiState.value.copy(isRepeatEnabled = isRepeatEnabled)
        appendNextToTimeline()
    }

    /** Prefers a genuinely-native skip (instant, works from the lock screen too) when the
     *  next item has already been appended; falls back to a fresh resolve otherwise. */
    fun skipToNext() {
        val current = controller ?: return
        if (current.hasNextMediaItem()) {
            current.seekToNext()
        } else if (advanceOrderPosition()) {
            playCurrentAtOrderPosition(seekPositionMs = 0L)
        }
    }

    fun skipToPrevious() {
        val positionMs = controller?.currentPosition ?: _uiState.value.positionMs
        if (positionMs > RESTART_INSTEAD_OF_PREVIOUS_THRESHOLD_MS || orderPosition <= 0) {
            controller?.seekTo(0)
            _uiState.value = _uiState.value.copy(positionMs = 0L)
        } else {
            orderPosition -= 1
            playCurrentAtOrderPosition(seekPositionMs = 0L)
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _uiState.value = _uiState.value.copy(positionMs = positionMs)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /** Moves [orderPosition] to the next entry, wrapping to the start if repeat is on.
     *  Returns false (and leaves position untouched) if there's nowhere to go. */
    private fun advanceOrderPosition(): Boolean {
        val nextPos = orderPosition + 1
        return when {
            nextPos <= playOrder.lastIndex -> {
                orderPosition = nextPos
                true
            }
            isRepeatEnabled && playOrder.isNotEmpty() -> {
                orderPosition = 0
                true
            }
            else -> false
        }
    }

    private fun buildPlayOrder(indices: List<Int>, pinnedFirst: Int, shuffle: Boolean): List<Int> {
        if (!shuffle) return indices
        val rest = indices.filter { it != pinnedFirst }.shuffled()
        return listOf(pinnedFirst) + rest
    }

    private fun playCurrentAtOrderPosition(seekPositionMs: Long) {
        val queueIndex = currentQueueIndex
        val track = queue.getOrNull(queueIndex) ?: return
        playJob?.cancel()
        appendJob?.cancel()

        _uiState.value = _uiState.value.copy(
            isBuffering = true,
            needsResolveToResume = false,
            videoId = track.videoId,
            title = track.title,
            thumbnailUrl = track.thumbnailUrl,
            url = track.url,
            positionMs = seekPositionMs,
            durationMs = track.durationSeconds * 1000L,
            hasNext = false,
            errorMessage = null,
            chapters = emptyList()
        )
        refreshUpNext()
        playbackHistoryPreferences.recordPlay(track)

        playJob = scope.launch {
            try {
                val resolved = withContext(Dispatchers.IO) { resolveTrack(track) }
                controller?.apply {
                    setMediaItem(buildMediaItem(resolved))
                    prepare()
                    if (seekPositionMs > 0) seekTo(seekPositionMs)
                    play()
                }
                _uiState.value = _uiState.value.copy(chapters = resolved.chapters)
                persistPlaybackState()
                appendNextToTimeline()
            } catch (e: AudioResolutionException) {
                _uiState.value = _uiState.value.copy(isBuffering = false, errorMessage = e.message)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBuffering = false,
                    errorMessage = "Couldn't load this track (${e.message ?: e::class.simpleName})."
                )
            }
        }
    }

    /** Resolves the upcoming track (following [playOrder], wrapping if repeat is on) and
     *  appends it to the live ExoPlayer timeline (rather than just caching it) so native
     *  seek-to-next -- including from the system notification -- has something to jump to. */
    private fun appendNextToTimeline() {
        val nextPos = orderPosition + 1
        val nextQueueIndex = when {
            nextPos <= playOrder.lastIndex -> playOrder[nextPos]
            isRepeatEnabled && playOrder.isNotEmpty() -> playOrder[0]
            else -> null
        }
        val nextTrack = nextQueueIndex?.let { queue.getOrNull(it) }
        if (nextTrack == null) {
            _uiState.value = _uiState.value.copy(hasNext = false)
            return
        }
        appendJob = scope.launch {
            try {
                val resolved = withContext(Dispatchers.IO) { resolveTrack(nextTrack) }
                controller?.addMediaItem(buildMediaItem(resolved))
                pendingNextVideoId = resolved.videoId
                pendingNextChapters = resolved.chapters
                _uiState.value = _uiState.value.copy(hasNext = true)
            } catch (_: Exception) {
                // Best-effort only. skipToNext() falls back to an on-demand resolve,
                // and the STATE_ENDED handler falls back for natural end-of-track too.
                _uiState.value = _uiState.value.copy(hasNext = false)
            }
        }
    }

    /** Downloaded tracks play straight from disk (also works fully offline); everything
     *  else is resolved fresh over the network since stream URLs are short-lived.
     *  [TrackEntity.localFilePath] is either a plain filesystem path (app-private storage, the
     *  default) or a `content://` URI string (a custom download location the user picked via
     *  Storage Access Framework, see [com.vibesync.app.data.DownloadLocationPreferences]) --
     *  Media3's `DefaultDataSource` already handles both `file://` and `content://` schemes
     *  natively, so no special-casing is needed on the playback side beyond building the right
     *  URI and checking existence the right way for each. */
    private suspend fun resolveTrack(track: TrackEntity): ResolvedAudio {
        val fresh = libraryRepository.getTrack(track.videoId) ?: track
        val localPath = fresh.localFilePath
        if (fresh.isDownloaded && localPath != null) {
            val isContentUri = localPath.startsWith("content://")
            val exists = if (isContentUri) {
                androidx.documentfile.provider.DocumentFile.fromSingleUri(appContext, Uri.parse(localPath))?.exists() == true
            } else {
                File(localPath).exists()
            }
            if (exists) {
                return ResolvedAudio(
                    videoId = fresh.videoId,
                    canonicalUrl = fresh.url,
                    title = fresh.title,
                    thumbnailUrl = fresh.thumbnailUrl,
                    durationSeconds = fresh.durationSeconds,
                    streamUrl = if (isContentUri) localPath else Uri.fromFile(File(localPath)).toString(),
                    // Not used for playback (only downloads care about this field) -- reflects
                    // whatever's actually already on disk, just to satisfy the data class.
                    fileExtension = localPath.substringAfterLast('.', "m4a")
                )
            }
        }
        return AudioStreamResolver.resolve(fresh.url)
    }

    private fun buildMediaItem(resolved: ResolvedAudio): MediaItem =
        MediaItem.Builder()
            .setMediaId(resolved.videoId)
            .setUri(resolved.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(resolved.title)
                    .apply { resolved.thumbnailUrl?.let { setArtworkUri(Uri.parse(it)) } }
                    .build()
            )
            .build()

    private fun restoreLastPlayed() {
        scope.launch {
            val saved = withContext(Dispatchers.IO) { playbackStateDao.get() } ?: return@launch
            val tracks = withContext(Dispatchers.IO) {
                when (saved.queuePlaylistId) {
                    DOWNLOADS_QUEUE_ID -> libraryRepository.getDownloadedTracksOnce()
                    LIKED_QUEUE_ID -> libraryRepository.getLikedTracksOnce()
                    else -> libraryRepository.getTracksForPlaylistOnce(saved.queuePlaylistId)
                }
            }
            if (tracks.isEmpty()) return@launch
            val index = tracks.indexOfFirst { it.videoId == saved.videoId }.takeIf { it >= 0 } ?: 0
            val track = tracks[index]

            queue = tracks
            playOrder = tracks.indices.toList()
            orderPosition = index
            queuePlaylistId = saved.queuePlaylistId

            _uiState.value = PlaybackUiState(
                isPlaying = false,
                isBuffering = false,
                needsResolveToResume = true,
                videoId = track.videoId,
                title = track.title,
                thumbnailUrl = track.thumbnailUrl,
                url = track.url,
                positionMs = saved.positionMs,
                durationMs = track.durationSeconds * 1000L,
                hasNext = false,
                queueLabel = when (saved.queuePlaylistId) {
                    DOWNLOADS_QUEUE_ID -> "Downloads"
                    LIKED_QUEUE_ID -> "Liked Songs"
                    else -> ""
                }
            )
            refreshUpNext()
        }
    }

    private fun persistPlaybackState() {
        val playlistId = queuePlaylistId ?: return
        val videoId = _uiState.value.videoId ?: return
        val positionMs = controller?.currentPosition ?: _uiState.value.positionMs
        scope.launch {
            withContext(Dispatchers.IO) {
                playbackStateDao.save(
                    PlaybackStateEntity(
                        videoId = videoId,
                        queuePlaylistId = playlistId,
                        queueIndex = currentQueueIndex,
                        positionMs = positionMs,
                        updatedAtEpochMillis = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun syncDuration() {
        val duration = controller?.duration ?: return
        if (duration > 0) {
            _uiState.value = _uiState.value.copy(durationMs = duration)
        }
    }

    private fun startPositionTicker() {
        scope.launch {
            var tick = 0
            while (true) {
                delay(500)
                val current = controller
                if (current != null && current.isPlaying) {
                    _uiState.value = _uiState.value.copy(positionMs = current.currentPosition.coerceAtLeast(0))
                    tick += 1
                    if (tick % STATE_SAVE_INTERVAL_TICKS == 0) persistPlaybackState()
                }
            }
        }
    }
}
