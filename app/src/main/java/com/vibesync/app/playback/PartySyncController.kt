package com.vibesync.app.playback

import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.data.party.PartyDoc
import com.vibesync.app.data.party.PartyQueueItem
import com.vibesync.app.data.party.PartyRepository
import com.vibesync.app.data.party.QueueItemStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** A party queue plays through the same single-track "queue of one" mechanism the app already
 *  uses for ad-hoc direct-play (see [com.vibesync.app.data.ADHOC_QUEUE_ID]) -- party tracks are
 *  never written to the local Room library. */
const val PARTY_QUEUE_ID = -4L

/** Snap back onto the shared position if a device has drifted more than this far from where it
 *  should be -- small natural jitter (buffering, decode timing) shouldn't cause visible re-seeks.
 *  Tightened from 1200ms/8s to 700ms/5s: with more people in a party, more devices are
 *  independently buffering/decoding at slightly different rates, so catching drift sooner keeps
 *  everyone closer together as the party scales past a couple of phones. */
private const val DRIFT_TOLERANCE_MS = 700L
private const val DRIFT_CHECK_INTERVAL_MS = 5_000L

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
class PartySyncController(
    private val partyRepository: PartyRepository,
    private val playerController: PlayerController
) {
    private var job: Job? = null

    @Volatile private var latestParty: PartyDoc? = null
    @Volatile private var latestQueue: List<PartyQueueItem> = emptyList()
    private var lastAppliedVideoId: String? = null
    /** Changes on *every* discrete party event -- play, pause, seek, and track-change all write a
     *  fresh server timestamp here (see `PartyRepository.setPlaybackState`/`setCurrentTrack`), so
     *  comparing this single field is enough to detect "something just happened, snap exactly"
     *  without needing separate bookkeeping for play-state vs. seek-while-playing. */
    private var lastAppliedPlaybackStartedAtMillis: Long? = null
    private var transitionInProgress = false
    private var partyCode: String? = null
    private var isHostDevice: Boolean = false
    /** The videoId [playerController] is actually showing right now, tracked independently of
     *  [lastAppliedVideoId] (which only changes when *this class* drives a change) -- lets
     *  [applyPartyState] tell "the player already got here on its own, via a local gapless
     *  auto-advance" apart from "this is a genuinely new track nobody's device has seen yet". */
    private var lastSeenLocalVideoId: String? = null

    /** True from the moment a party is joined/created until this device has actually caught up to
     *  wherever the party currently is (or immediately, if nothing's playing yet) -- drives the
     *  "Joining party..." screen so nobody hears a stale/out-of-sync moment of audio while the
     *  stream is still resolving. */
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun start(code: String, isHost: Boolean, scope: CoroutineScope) {
        stop()
        _isSyncing.value = true
        partyCode = code
        isHostDevice = isHost
        // Every child coroutine below is wrapped defensively -- this all runs on viewModelScope,
        // which has no supervisor/catch-all of its own, so *any* uncaught exception here (a
        // Firestore listener error, a PlayerController call hitting an edge case this brand-new
        // code path hasn't seen before) would otherwise cancel the whole scope and crash the app,
        // not just this feature. A party sync hiccup should degrade quietly, not take the app down.
        job = scope.launch {
            launch {
                try {
                    partyRepository.observeParty(code).collect { party ->
                        latestParty = party
                        if (party != null) {
                            if (party.currentVideoId == null) {
                                // Nothing playing yet (a freshly-created party) -- nothing to catch
                                // up to, so "joining" is instantly done.
                                _isSyncing.value = false
                            } else {
                                try {
                                    applyPartyState(party)
                                } catch (e: Exception) {
                                    android.util.Log.e("PartySync", "applyPartyState failed", e)
                                }
                            }
                            updatePartyPrefetch()
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PartySync", "observeParty failed", e)
                }
            }
            launch {
                try {
                    partyRepository.observeQueue(code).collect { items ->
                        latestQueue = items
                        updatePartyPrefetch()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PartySync", "observeQueue failed", e)
                }
            }
            launch {
                try {
                    driftCheckLoop()
                } catch (e: Exception) {
                    android.util.Log.e("PartySync", "driftCheckLoop failed", e)
                }
            }
            launch {
                try {
                    watchLocalTransitions()
                } catch (e: Exception) {
                    android.util.Log.e("PartySync", "watchLocalTransitions failed", e)
                }
            }
            if (isHost) {
                launch {
                    try {
                        hostAdvanceLoop(code)
                    } catch (e: Exception) {
                        android.util.Log.e("PartySync", "hostAdvanceLoop failed", e)
                    }
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        latestParty = null
        latestQueue = emptyList()
        lastAppliedVideoId = null
        lastAppliedPlaybackStartedAtMillis = null
        lastSeenLocalVideoId = null
        partyCode = null
        isHostDevice = false
        playerController.setPartyUpcoming(null)
        _isSyncing.value = false
    }

    /** The party's live expected position right now, or null if there's no active party/track --
     *  exposed so `MainViewModel` can catch a device back up to the party (e.g. when a non-host
     *  member resumes local playback after pausing it) without duplicating this formula. */
    fun currentExpectedPositionMs(): Long? = latestParty?.let { expectedPosition(it) }

    private suspend fun applyPartyState(party: PartyDoc) {
        val videoId = party.currentVideoId ?: return
        val isNewTrack = videoId != lastAppliedVideoId
        val isDiscreteEvent = !isNewTrack &&
            lastAppliedPlaybackStartedAtMillis != null &&
            lastAppliedPlaybackStartedAtMillis != party.playbackStartedAtMillis
        lastAppliedPlaybackStartedAtMillis = party.playbackStartedAtMillis

        if (isNewTrack) {
            lastAppliedVideoId = videoId
            val track = TrackEntity(
                videoId = videoId,
                url = party.currentUrl ?: "",
                title = party.currentTitle ?: "",
                thumbnailUrl = party.currentThumbnailUrl,
                durationSeconds = party.currentDurationSeconds,
                addedAtEpochMillis = 0L
            )
            playerController.playQueue(PARTY_QUEUE_ID, listOf(track), 0, label = party.name)
            // Resolving/buffering the stream takes real time -- recompute the expected position
            // *after* that instead of using the value from before the load, so a join that took
            // 5 seconds to load lands wherever the party actually is now, not where it was.
            playerController.seekTo(expectedPosition(party))
            if (!party.isPlaying && playerController.uiState.value.isPlaying) {
                playerController.togglePlayPause()
            }
            _isSyncing.value = false
        } else if (isDiscreteEvent) {
            // A fresh playbackStartedAt on the SAME track means play, pause, or a manual seek just
            // happened -- always snap to the exact position rather than only correcting past the
            // drift tolerance, so "stop and play again" (or the host scrubbing the timeline)
            // reliably lands exactly back in sync instead of "usually, unless". Order matters here
            // and depends on direction: pausing should stop audio FIRST, then reposition while
            // silent (seeking first left a brief window where audio kept playing at the old spot
            // before the pause landed, which is the likely cause of a reported "stops then
            // instantly starts again" blip); resuming should land on the right position BEFORE
            // audio starts, same as it already did.
            val current = playerController.uiState.value
            if (party.isPlaying) {
                playerController.seekTo(expectedPosition(party))
                if (!current.isPlaying) playerController.togglePlayPause()
            } else {
                if (current.isPlaying) playerController.togglePlayPause()
                playerController.seekTo(expectedPosition(party))
            }
        } else {
            val current = playerController.uiState.value
            if (current.isPlaying != party.isPlaying) playerController.togglePlayPause()
            val drift = kotlin.math.abs(current.positionMs - expectedPosition(party))
            if (drift > DRIFT_TOLERANCE_MS) playerController.seekTo(expectedPosition(party))
        }
    }

    /** Runs on every device (not just the host): re-checks drift on a timer, independent of
     *  whatever Firestore snapshot events happen to arrive. [applyPartyState] only ever runs
     *  reactively -- if a track plays uninterrupted for minutes with no pause/seek/track-change,
     *  no new snapshot arrives and no drift check happens in that whole window, even though
     *  consumer audio hardware clocks aren't perfectly matched between phones and *will* drift
     *  apart slowly during exactly that kind of long, uninterrupted stretch. This is very likely
     *  the actual explanation for a "slight audio mismatch between two users" report that shows up
     *  gradually rather than right after some obvious event -- there was previously no mechanism
     *  correcting for it at all outside of a discrete event happening to occur. */
    private suspend fun driftCheckLoop() {
        while (kotlin.coroutines.coroutineContext.isActive) {
            delay(DRIFT_CHECK_INTERVAL_MS)
            try {
                val party = latestParty ?: continue
                if (party.currentVideoId == null || party.currentVideoId != lastAppliedVideoId) continue
                val current = playerController.uiState.value
                if (current.isPlaying != party.isPlaying) continue // a discrete event is already mid-flight, don't fight it
                val drift = kotlin.math.abs(current.positionMs - expectedPosition(party))
                if (drift > DRIFT_TOLERANCE_MS) playerController.seekTo(expectedPosition(party))
            } catch (e: Exception) {
                android.util.Log.e("PartySync", "driftCheckLoop tick failed", e)
            }
        }
    }

    /** Tells [playerController] what the party queue's next approved item is right now, so it can
     *  keep it pre-buffered on the live ExoPlayer timeline (see
     *  [PlayerController.setPartyUpcoming]). Called on every party-doc and every queue update --
     *  cheap and idempotent, [setPartyUpcoming] itself no-ops if nothing actually changed. */
    private fun updatePartyPrefetch() {
        val party = latestParty ?: return
        if (party.currentVideoId == null) return
        val next = latestQueue
            .filter { it.status == QueueItemStatus.APPROVED && it.id != party.currentQueueItemId }
            .minByOrNull { it.position }
        playerController.setPartyUpcoming(next?.toTrackEntity())
    }

    /** Watches [playerController]'s own reported "now playing" videoId for a change that *we*
     *  didn't just cause via [applyPartyState]'s reload path -- that's exactly what a natural
     *  gapless auto-advance onto the prefetched next track (see [setPartyUpcoming]) looks like
     *  from here. Two things happen the moment that's detected: bookkeeping catches up immediately
     *  ([lastAppliedVideoId] set right away, not only once Firestore's own snapshot for this track
     *  eventually arrives) so [applyPartyState] recognizes "already there" and reconciles position
     *  instead of reloading the same track a second time from the network; and on the host's
     *  device specifically, the party doc gets updated right now instead of waiting on
     *  [hostAdvanceLoop]'s slower poll -- which is what makes every other device's own local
     *  auto-advance (they're all independently prefetching the same next queue item) land at
     *  nearly the same real-world moment instead of however many seconds behind the poll interval. */
    private suspend fun watchLocalTransitions() {
        playerController.uiState.map { it.videoId }.distinctUntilChanged().collect { videoId ->
            val previous = lastSeenLocalVideoId
            lastSeenLocalVideoId = videoId
            if (videoId == null || previous == null) return@collect
            lastAppliedVideoId = videoId
            val code = partyCode ?: return@collect
            if (isHostDevice && videoId != latestParty?.currentVideoId) {
                val item = latestQueue.firstOrNull { it.videoId == videoId && it.status == QueueItemStatus.APPROVED }
                if (item != null) transitionForward(code, item)
            }
        }
    }

    /** Host-only: watches for the current track actually finishing (not a manual pause) and moves
     *  on to the next approved queue item. */
    private suspend fun hostAdvanceLoop(code: String) {
        while (kotlin.coroutines.coroutineContext.isActive) {
            delay(DRIFT_CHECK_INTERVAL_MS)
            try {
                val party = latestParty ?: continue
                val state = playerController.uiState.value
                val trackEnded = !state.isPlaying &&
                    state.durationMs > 0 &&
                    state.positionMs >= state.durationMs - 500 &&
                    party.isPlaying // party doc still thinks it's playing -- genuinely ended, not a host pause

                if (trackEnded && !transitionInProgress) {
                    if (!playNext(code)) {
                        partyRepository.setPlaybackState(code, isPlaying = false, positionMs = state.durationMs)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PartySync", "hostAdvanceLoop tick failed", e)
            }
        }
    }

    /** Forward transitions (direct-pick, auto-advance, skip-next): retires whatever was current
     *  to PLAYED (if it corresponds to a real queue item), then makes [item] current. [item]'s own
     *  status is left as-is -- direct-pick and skip-next both always target an already-APPROVED
     *  item, so there's nothing to normalize on the way in (unlike [playPrevious], which does need
     *  to un-PLAY the item it's bringing back). */
    private suspend fun transitionForward(code: String, item: PartyQueueItem) {
        if (transitionInProgress) return
        transitionInProgress = true
        try {
            val previousId = latestParty?.currentQueueItemId
            if (previousId != null && previousId != item.id) {
                partyRepository.markPlayed(code, previousId)
            }
            partyRepository.setCurrentTrack(code, item.toTrackEntity(), item.id, startPositionMs = 0L)
        } finally {
            transitionInProgress = false
        }
    }

    /** Host action: jump straight to any approved queue item, skipping whatever order it was in. */
    suspend fun playQueueItem(code: String, item: PartyQueueItem) = transitionForward(code, item)

    /** Host action (also used by the auto-advance loop): move to the next approved item by
     *  position. Returns false if the queue is empty, so the caller can stop playback instead. */
    suspend fun playNext(code: String): Boolean {
        val next = latestQueue.filter { it.status == QueueItemStatus.APPROVED }.minByOrNull { it.position } ?: return false
        transitionForward(code, next)
        return true
    }

    /** Host action: go back to whatever played immediately before the current track. Deliberately
     *  NOT built on [transitionForward] -- going backward needs to *un-PLAY* the track it's
     *  returning to (so a later `playPrevious` doesn't pick the same track again) while *also*
     *  re-approving the track being left behind at the front of "Up next" (not marking it PLAYED,
     *  which is what the forward path would do) so skipping forward again lands right back where
     *  you were. Trying to force both directions through one shared function is what caused an
     *  early draft of this to silently overwrite its own writes -- kept as two separate,
     *  intentionally-not-unified code paths instead. Returns false if there's no history yet. */
    suspend fun playPrevious(code: String): Boolean {
        if (transitionInProgress) return false
        val previousTrack = latestQueue.filter { it.status == QueueItemStatus.PLAYED }.maxByOrNull { it.playedAtMillis } ?: return false
        transitionInProgress = true
        try {
            val outgoingId = latestParty?.currentQueueItemId
            if (outgoingId != null && outgoingId != previousTrack.id) {
                val minApprovedPosition = latestQueue.filter { it.status == QueueItemStatus.APPROVED }.minOfOrNull { it.position } ?: 0L
                partyRepository.markApproved(code, outgoingId, minApprovedPosition - 1)
            }
            partyRepository.markApproved(code, previousTrack.id, 0L) // position irrelevant while current
            partyRepository.setCurrentTrack(code, previousTrack.toTrackEntity(), previousTrack.id, startPositionMs = 0L)
        } finally {
            transitionInProgress = false
        }
        return true
    }

    private fun PartyQueueItem.toTrackEntity() = TrackEntity(
        videoId = videoId,
        url = url,
        title = title,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        addedAtEpochMillis = 0L
    )

    private fun expectedPosition(party: PartyDoc): Long {
        if (!party.isPlaying) return party.startPositionMs
        val elapsed = System.currentTimeMillis() - party.playbackStartedAtMillis
        return (party.startPositionMs + elapsed).coerceAtLeast(0L)
    }
}
