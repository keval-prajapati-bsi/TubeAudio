package com.vibesync.app.playback

import com.vibesync.app.extractor.Chapter

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    /** True right after restoring the last-played track on app launch: shown as a
     *  paused placeholder until the user presses play, since the stream URL has
     *  to be re-resolved (the previous one has long since expired). */
    val needsResolveToResume: Boolean = false,
    val videoId: String? = null,
    val title: String? = null,
    val thumbnailUrl: String? = null,
    /** The original YouTube watch link (never the resolved stream URL) -- used by the Now
     *  Playing screen's "Copy link" action. */
    val url: String? = null,
    /** What queue is currently playing ("All songs", "Downloads", "Liked Songs", a custom
     *  playlist's name...), shown as "Playing from {queueLabel}" on the expanded Now Playing
     *  screen. Empty when not meaningful (e.g. an ad-hoc direct play). */
    val queueLabel: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    /** True only once the next track is genuinely queued in the player (not just
     *  "there logically is a next track") -- see PlayerController's class doc. */
    val hasNext: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val isRepeatEnabled: Boolean = false,
    val errorMessage: String? = null,
    /** YouTube's own chapter markers for the current track, if it has any (most don't -- an
     *  empty list is normal). Populated once stream resolution finishes, cleared the moment a
     *  new track starts loading so the old track's chapters don't briefly show against it. */
    val chapters: List<Chapter> = emptyList()
)
