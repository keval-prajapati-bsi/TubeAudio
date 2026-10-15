package com.vibesync.app.playback

import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.data.localparty.LocalRoomState
import com.vibesync.app.localparty.LocalPartyClient
import com.vibesync.app.localparty.LocalPartyHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** A local room plays through the same single-track "queue of one" mechanism the Firebase party
 *  used before this app's gapless-prefetch fix (see [PARTY_QUEUE_ID]'s sibling constant here) --
 *  a deliberate v1 simplification, not an oversight: porting the full prefetch/gapless mechanism
 *  to a second, socket-driven sync engine in the same round as building the socket layer itself
 *  was more risk than this round's budget could absorb. A short poll-driven gap between songs is
 *  the known, disclosed trade-off -- see version27/CHANGES.txt. Worth revisiting the same way the
 *  Firebase party's gap was fixed, once this is confirmed working end-to-end on real devices. */
const val LOCAL_PARTY_QUEUE_ID = -5L

private const val LOCAL_DRIFT_TOLERANCE_MS = 700L
private const val LOCAL_CHECK_INTERVAL_MS = 2_000L

/**
 * The local room's equivalent of [PartySyncController] -- same expected-position formula
 * (`startPositionMs + (now - playbackStartedAtMillis)` while playing), same discrete-event/drift
 * correction shape, just fed by [LocalPartyHost]/[LocalPartyClient]'s in-memory/socket state
 * instead of Firestore snapshots. Both host and guest run the exact same [applyLocalState] against
 * their own local copy of the room state -- for the host that's [LocalPartyHost.state] itself (a
 * same-process `StateFlow`, so "the host reacts to its own broadcast" costs nothing and needs no
 * special-casing, exactly like the Firebase host reacting to its own echoed Firestore snapshot).
 */
class LocalPartySyncController(private val playerController: PlayerController) {
    private var job: Job? = null
    private var lastAppliedVideoId: String? = null
    private var lastAppliedPlaybackStartedAtMillis: Long? = null

    fun startAsHost(host: LocalPartyHost, scope: CoroutineScope) {
        stop()
        job = scope.launch {
            launch {
                host.state.collect { state ->
                    try { applyLocalState(state) } catch (e: Exception) {
                        android.util.Log.e("LocalPartySync", "applyLocalState (host) failed", e)
                    }
                }
            }
            launch {
                try { hostAdvanceLoop(host) } catch (e: Exception) {
                    android.util.Log.e("LocalPartySync", "hostAdvanceLoop failed", e)
                }
            }
        }
    }

    fun startAsGuest(client: LocalPartyClient, scope: CoroutineScope) {
        stop()
        job = scope.launch {
            launch {
                client.state.collect { state ->
                    if (state == null) return@collect
                    try { applyLocalState(state) } catch (e: Exception) {
                        android.util.Log.e("LocalPartySync", "applyLocalState (guest) failed", e)
                    }
                }
            }
            launch {
                try { driftCheckLoop(client) } catch (e: Exception) {
                    android.util.Log.e("LocalPartySync", "driftCheckLoop failed", e)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        lastAppliedVideoId = null
        lastAppliedPlaybackStartedAtMillis = null
    }

    private fun applyLocalState(state: LocalRoomState) {
        val videoId = state.currentVideoId ?: return
        val isNewTrack = videoId != lastAppliedVideoId
        val isDiscreteEvent = !isNewTrack &&
            lastAppliedPlaybackStartedAtMillis != null &&
            lastAppliedPlaybackStartedAtMillis != state.playbackStartedAtMillis
        lastAppliedPlaybackStartedAtMillis = state.playbackStartedAtMillis

        if (isNewTrack) {
            lastAppliedVideoId = videoId
            val track = TrackEntity(
                videoId = videoId,
                url = state.currentUrl ?: "",
                title = state.currentTitle ?: "",
                thumbnailUrl = state.currentThumbnailUrl,
                durationSeconds = state.currentDurationSeconds,
                addedAtEpochMillis = 0L
            )
            playerController.playQueue(LOCAL_PARTY_QUEUE_ID, listOf(track), 0, label = state.roomName)
            playerController.seekTo(expectedPosition(state))
            if (!state.isPlaying && playerController.uiState.value.isPlaying) playerController.togglePlayPause()
        } else if (isDiscreteEvent) {
            val current = playerController.uiState.value
            if (state.isPlaying) {
                playerController.seekTo(expectedPosition(state))
                if (!current.isPlaying) playerController.togglePlayPause()
            } else {
                if (current.isPlaying) playerController.togglePlayPause()
                playerController.seekTo(expectedPosition(state))
            }
        } else {
            val current = playerController.uiState.value
            if (current.isPlaying != state.isPlaying) playerController.togglePlayPause()
        }
    }

    private suspend fun driftCheckLoop(client: LocalPartyClient) {
        while (kotlin.coroutines.coroutineContext.isActive) {
            delay(LOCAL_CHECK_INTERVAL_MS)
            val state = client.state.value ?: continue
            if (state.currentVideoId == null || state.currentVideoId != lastAppliedVideoId) continue
            val current = playerController.uiState.value
            if (current.isPlaying != state.isPlaying) continue
            val drift = kotlin.math.abs(current.positionMs - expectedPosition(state))
            if (drift > LOCAL_DRIFT_TOLERANCE_MS) playerController.seekTo(expectedPosition(state))
        }
    }

    /** Host-only: watches for the current track actually finishing and moves on to the front of
     *  the queue -- the poll-based mechanism the Firebase party used before its gapless fix (see
     *  this class's doc comment for why the local room doesn't have that fix yet). */
    private suspend fun hostAdvanceLoop(host: LocalPartyHost) {
        while (kotlin.coroutines.coroutineContext.isActive) {
            delay(LOCAL_CHECK_INTERVAL_MS)
            val playback = playerController.uiState.value
            val room = host.state.value
            val trackEnded = !playback.isPlaying &&
                playback.durationMs > 0 &&
                playback.positionMs >= playback.durationMs - 500 &&
                room.isPlaying
            if (trackEnded) {
                val next = room.queue.firstOrNull()
                if (next != null) {
                    host.setCurrentTrack(next.videoId, next.title, next.thumbnailUrl, next.url, next.durationSeconds, fromQueueItemId = next.id)
                } else {
                    host.setPlaybackState(isPlaying = false, positionMs = playback.durationMs)
                }
            }
        }
    }

    private fun expectedPosition(state: LocalRoomState): Long {
        if (!state.isPlaying) return state.startPositionMs
        val elapsed = System.currentTimeMillis() - state.playbackStartedAtMillis
        return (state.startPositionMs + elapsed).coerceAtLeast(0L)
    }
}
