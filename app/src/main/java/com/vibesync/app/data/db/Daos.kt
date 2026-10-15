package com.vibesync.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE videoId = :videoId")
    suspend fun delete(videoId: String)

    @Query("SELECT * FROM tracks WHERE videoId = :videoId")
    suspend fun getByVideoId(videoId: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' ORDER BY addedAtEpochMillis DESC")
    fun search(query: String): Flow<List<TrackEntity>>

    @Query("UPDATE tracks SET isDownloaded = 1, localFilePath = :path WHERE videoId = :videoId")
    suspend fun markDownloaded(videoId: String, path: String)

    @Query("UPDATE tracks SET isDownloaded = 0, localFilePath = NULL WHERE videoId = :videoId")
    suspend fun clearDownload(videoId: String)

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC")
    fun observeDownloaded(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC")
    suspend fun getDownloadedOnce(): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC")
    fun observeLiked(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC")
    suspend fun getLikedOnce(): List<TrackEntity>

    @Query("UPDATE tracks SET isLiked = :liked WHERE videoId = :videoId")
    suspend fun setLiked(videoId: String, liked: Boolean)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY isDefault DESC, createdAtEpochMillis ASC")
    fun observePlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE lastPlayedAtEpochMillis IS NOT NULL ORDER BY lastPlayedAtEpochMillis DESC LIMIT 8")
    fun observeRecentPlaylists(): Flow<List<PlaylistEntity>>

    @Insert
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultPlaylist(): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getById(id: Long): PlaylistEntity?

    @Query("UPDATE playlists SET name = :name WHERE id = :id AND isDefault = 0")
    suspend fun rename(id: Long, name: String)

    @Query("DELETE FROM playlists WHERE id = :id AND isDefault = 0")
    suspend fun delete(id: Long)

    @Query("UPDATE playlists SET lastPlayedAtEpochMillis = :timestamp WHERE id = :id")
    suspend fun touchLastPlayed(id: Long, timestamp: Long)
}

@Dao
interface PlaylistTrackDao {
    @Query(
        """
        SELECT tracks.* FROM tracks
        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position ASC
        """
    )
    fun observeTracksForPlaylist(playlistId: Long): Flow<List<TrackEntity>>

    @Query(
        """
        SELECT tracks.* FROM tracks
        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position ASC
        """
    )
    suspend fun getTracksForPlaylistOnce(playlistId: Long): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun observeTrackCount(playlistId: Long): Flow<Int>

    @Query(
        """
        SELECT tracks.thumbnailUrl FROM tracks
        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position ASC
        LIMIT 1
        """
    )
    fun observeCoverThumbnail(playlistId: Long): Flow<String?>

    @Query("SELECT MAX(position) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun maxPosition(playlistId: Long): Int?

    @Query("UPDATE playlist_tracks SET position = position + 1 WHERE playlistId = :playlistId")
    suspend fun shiftAllUp(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun remove(playlistId: Long, videoId: String)

    @Query("SELECT playlistId FROM playlist_tracks WHERE videoId = :videoId")
    suspend fun playlistIdsForTrack(videoId: String): List<Long>

    @Transaction
    suspend fun prependToDefault(playlistId: Long, videoId: String) {
        shiftAllUp(playlistId)
        insert(PlaylistTrackCrossRef(playlistId, videoId, 0, System.currentTimeMillis()))
    }

    @Transaction
    suspend fun appendToPlaylist(playlistId: Long, videoId: String) {
        val nextPosition = (maxPosition(playlistId) ?: -1) + 1
        insert(PlaylistTrackCrossRef(playlistId, videoId, nextPosition, System.currentTimeMillis()))
    }

    @Query("UPDATE playlist_tracks SET position = :position WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun updatePosition(playlistId: Long, videoId: String, position: Int)

    @Transaction
    suspend fun reorder(playlistId: Long, orderedVideoIds: List<String>) {
        orderedVideoIds.forEachIndexed { index, videoId -> updatePosition(playlistId, videoId, index) }
    }
}

@Dao
interface PlaybackStateDao {
    @Query("SELECT * FROM playback_state WHERE id = 0")
    suspend fun get(): PlaybackStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(state: PlaybackStateEntity)
}
