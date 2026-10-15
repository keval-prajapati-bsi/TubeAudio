package com.vibesync.app.data.db;

/**
 * Many-to-many link between playlists and tracks, with per-playlist ordering.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lcom/vibesync/app/data/db/PlaylistTrackCrossRef;", "", "playlistId", "", "videoId", "", "position", "", "addedAtEpochMillis", "(JLjava/lang/String;IJ)V", "getAddedAtEpochMillis", "()J", "getPlaylistId", "getPosition", "()I", "getVideoId", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
@androidx.room.Entity(tableName = "playlist_tracks", primaryKeys = {"playlistId", "videoId"}, foreignKeys = {@androidx.room.ForeignKey(entity = com.vibesync.app.data.db.PlaylistEntity.class, parentColumns = {"id"}, childColumns = {"playlistId"}, onDelete = 5), @androidx.room.ForeignKey(entity = com.vibesync.app.data.db.TrackEntity.class, parentColumns = {"videoId"}, childColumns = {"videoId"}, onDelete = 5)}, indices = {@androidx.room.Index(value = {"videoId"}), @androidx.room.Index(value = {"playlistId"})})
public final class PlaylistTrackCrossRef {
    private final long playlistId = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String videoId = null;
    private final int position = 0;
    private final long addedAtEpochMillis = 0L;
    
    public PlaylistTrackCrossRef(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, int position, long addedAtEpochMillis) {
        super();
    }
    
    public final long getPlaylistId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVideoId() {
        return null;
    }
    
    public final int getPosition() {
        return 0;
    }
    
    public final long getAddedAtEpochMillis() {
        return 0L;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.db.PlaylistTrackCrossRef copy(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, int position, long addedAtEpochMillis) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}