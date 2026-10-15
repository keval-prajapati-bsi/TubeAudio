package com.vibesync.app.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A resolved YouTube link. [url] is the canonical watch URL, never the resolved
 * audio stream URL -- those are short-lived signed URLs and must be re-resolved
 * on every replay.
 */
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val videoId: String,
    val url: String,
    val title: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val addedAtEpochMillis: Long,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val isLiked: Boolean = false
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAtEpochMillis: Long,
    val lastPlayedAtEpochMillis: Long? = null,
    val description: String? = null,
    /** A user-picked cover photo (via the system photo picker), stored as a persisted content
     *  URI string. Takes priority over the auto-derived "most recent track's thumbnail" cover
     *  when set. */
    val coverImageUri: String? = null,
    /** A user-picked accent color from "Create playlist"'s theme-color swatches, as ARGB. Null
     *  falls back to the existing hash-based tile color. */
    val themeColorArgb: Int? = null
)

/** Many-to-many link between playlists and tracks, with per-playlist ordering. */
@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "videoId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["videoId"],
            childColumns = ["videoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("videoId"), Index("playlistId")]
)
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val videoId: String,
    val position: Int,
    val addedAtEpochMillis: Long
)

/**
 * Single-row table (id is always 0) recording what to resume on next app launch:
 * which track, from which playlist/queue, and how far into it.
 */
@Entity(tableName = "playback_state")
data class PlaybackStateEntity(
    @PrimaryKey val id: Int = 0,
    val videoId: String,
    val queuePlaylistId: Long,
    val queueIndex: Int,
    val positionMs: Long,
    val updatedAtEpochMillis: Long
)
