package com.vibesync.app.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\t\u001a\u0004\u0018\u00010\bH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00100\u000fH\'J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00100\u000fH\'J\u001e\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0018\u00a8\u0006\u0019"}, d2 = {"Lcom/vibesync/app/data/db/PlaylistDao;", "", "delete", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "Lcom/vibesync/app/data/db/PlaylistEntity;", "getDefaultPlaylist", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "playlist", "(Lcom/vibesync/app/data/db/PlaylistEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observePlaylists", "Lkotlinx/coroutines/flow/Flow;", "", "observeRecentPlaylists", "rename", "name", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "touchLastPlayed", "timestamp", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface PlaylistDao {
    
    @androidx.room.Query(value = "SELECT * FROM playlists ORDER BY isDefault DESC, createdAtEpochMillis ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> observePlaylists();
    
    @androidx.room.Query(value = "SELECT * FROM playlists WHERE lastPlayedAtEpochMillis IS NOT NULL ORDER BY lastPlayedAtEpochMillis DESC LIMIT 8")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> observeRecentPlaylists();
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.PlaylistEntity playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM playlists WHERE isDefault = 1 LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDefaultPlaylist(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.PlaylistEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM playlists WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.PlaylistEntity> $completion);
    
    @androidx.room.Query(value = "UPDATE playlists SET name = :name WHERE id = :id AND isDefault = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object rename(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM playlists WHERE id = :id AND isDefault = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE playlists SET lastPlayedAtEpochMillis = :timestamp WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object touchLastPlayed(long id, long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}