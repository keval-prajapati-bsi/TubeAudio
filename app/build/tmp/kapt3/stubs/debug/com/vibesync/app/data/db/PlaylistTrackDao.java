package com.vibesync.app.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\bg\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0097@\u00a2\u0006\u0002\u0010\bJ\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00142\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00120\u00142\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u00142\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001e\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0097@\u00a2\u0006\u0002\u0010\bJ\u001e\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ$\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00070\nH\u0097@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ&\u0010\u001f\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010!\u00a8\u0006\""}, d2 = {"Lcom/vibesync/app/data/db/PlaylistTrackDao;", "", "appendToPlaylist", "", "playlistId", "", "videoId", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTracksForPlaylistOnce", "", "Lcom/vibesync/app/data/db/TrackEntity;", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "crossRef", "Lcom/vibesync/app/data/db/PlaylistTrackCrossRef;", "(Lcom/vibesync/app/data/db/PlaylistTrackCrossRef;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "maxPosition", "", "observeCoverThumbnail", "Lkotlinx/coroutines/flow/Flow;", "observeTrackCount", "observeTracksForPlaylist", "playlistIdsForTrack", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "prependToDefault", "remove", "reorder", "orderedVideoIds", "(JLjava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "shiftAllUp", "updatePosition", "position", "(JLjava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface PlaylistTrackDao {
    
    @androidx.room.Query(value = "\n        SELECT tracks.* FROM tracks\n        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n        WHERE playlist_tracks.playlistId = :playlistId\n        ORDER BY playlist_tracks.position ASC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> observeTracksForPlaylist(long playlistId);
    
    @androidx.room.Query(value = "\n        SELECT tracks.* FROM tracks\n        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n        WHERE playlist_tracks.playlistId = :playlistId\n        ORDER BY playlist_tracks.position ASC\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTracksForPlaylistOnce(long playlistId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> observeTrackCount(long playlistId);
    
    @androidx.room.Query(value = "\n        SELECT tracks.thumbnailUrl FROM tracks\n        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n        WHERE playlist_tracks.playlistId = :playlistId\n        ORDER BY playlist_tracks.position ASC\n        LIMIT 1\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.String> observeCoverThumbnail(long playlistId);
    
    @androidx.room.Query(value = "SELECT MAX(position) FROM playlist_tracks WHERE playlistId = :playlistId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object maxPosition(long playlistId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "UPDATE playlist_tracks SET position = position + 1 WHERE playlistId = :playlistId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object shiftAllUp(long playlistId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaylistTrackCrossRef crossRef, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object remove(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT playlistId FROM playlist_tracks WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object playlistIdsForTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object prependToDefault(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object appendToPlaylist(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE playlist_tracks SET position = :position WHERE playlistId = :playlistId AND videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePosition(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, int position, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object reorder(long playlistId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> orderedVideoIds, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object prependToDefault(@org.jetbrains.annotations.NotNull()
        com.vibesync.app.data.db.PlaylistTrackDao $this, long playlistId, @org.jetbrains.annotations.NotNull()
        java.lang.String videoId, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object appendToPlaylist(@org.jetbrains.annotations.NotNull()
        com.vibesync.app.data.db.PlaylistTrackDao $this, long playlistId, @org.jetbrains.annotations.NotNull()
        java.lang.String videoId, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object reorder(@org.jetbrains.annotations.NotNull()
        com.vibesync.app.data.db.PlaylistTrackDao $this, long playlistId, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> orderedVideoIds, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}