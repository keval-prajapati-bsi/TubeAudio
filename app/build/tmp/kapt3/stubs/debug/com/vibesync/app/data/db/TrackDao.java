package com.vibesync.app.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u000b0\u0012H\'J\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u000b0\u0012H\'J\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u000b0\u00122\u0006\u0010\u0015\u001a\u00020\u0005H\'J\u001e\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u0018H\u00a7@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006\u001d"}, d2 = {"Lcom/vibesync/app/data/db/TrackDao;", "", "clearDownload", "", "videoId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "getByVideoId", "Lcom/vibesync/app/data/db/TrackEntity;", "getDownloadedOnce", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLikedOnce", "markDownloaded", "path", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeDownloaded", "Lkotlinx/coroutines/flow/Flow;", "observeLiked", "search", "query", "setLiked", "liked", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsert", "track", "(Lcom/vibesync/app/data/db/TrackEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface TrackDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM tracks WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByVideoId(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE title LIKE \'%\' || :query || \'%\' ORDER BY addedAtEpochMillis DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> search(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "UPDATE tracks SET isDownloaded = 1, localFilePath = :path WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markDownloaded(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE tracks SET isDownloaded = 0, localFilePath = NULL WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> observeDownloaded();
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDownloadedOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> observeLiked();
    
    @androidx.room.Query(value = "SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLikedOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE tracks SET isLiked = :liked WHERE videoId = :videoId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setLiked(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, boolean liked, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}