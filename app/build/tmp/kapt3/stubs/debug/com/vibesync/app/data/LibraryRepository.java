package com.vibesync.app.data;

/**
 * Higher-level operations over the track/playlist/cross-ref DAOs. The "Default"
 * playlist is created once on first use and holds every track ever added, newest
 * first; custom playlists are user-created and hold tracks in append order.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0015\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010\u001c\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010&J\u0016\u0010\'\u001a\u00020!2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010(J:\u0010)\u001a\u00020\u00122\u0006\u0010*\u001a\u00020%2\n\b\u0002\u0010+\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010,\u001a\u0004\u0018\u00010%2\n\b\u0002\u0010-\u001a\u0004\u0018\u00010.H\u0086@\u00a2\u0006\u0002\u0010/J\u0016\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020#H\u0086@\u00a2\u0006\u0002\u00102J\u0016\u00103\u001a\u00020!2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010(J\u000e\u00104\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u00105J\u0016\u00106\u001a\u00020\b2\u0006\u00107\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u00108J\u0014\u00109\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0086@\u00a2\u0006\u0002\u00105J\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0086@\u00a2\u0006\u0002\u00105J\u0018\u0010;\u001a\u0004\u0018\u00010\b2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010(J\u001c\u0010<\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\"\u001a\u00020#H\u0086@\u00a2\u0006\u0002\u00102J$\u0010=\u001a\u00020\u00122\u0006\u0010*\u001a\u00020%2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020?0\u0007H\u0086@\u00a2\u0006\u0002\u0010@J\u001e\u0010A\u001a\u00020!2\u0006\u0010$\u001a\u00020%2\u0006\u0010B\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010CJ\u0016\u0010D\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010%0\u00062\u0006\u0010\"\u001a\u00020#J\u0014\u0010E\u001a\b\u0012\u0004\u0012\u00020.0\u00062\u0006\u0010\"\u001a\u00020#J\u001a\u0010F\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010\"\u001a\u00020#J\u001c\u0010G\u001a\b\u0012\u0004\u0012\u00020#0\u00072\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010(J\u001e\u0010H\u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010&J\u001e\u0010I\u001a\u00020!2\u0006\u00101\u001a\u00020#2\u0006\u0010*\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010&J$\u0010J\u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\f\u0010K\u001a\b\u0012\u0004\u0012\u00020%0\u0007H\u0086@\u00a2\u0006\u0002\u0010LJ\u001a\u0010M\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010N\u001a\u00020%J\u0016\u0010O\u001a\u00020!2\u0006\u0010$\u001a\u00020%H\u0086@\u00a2\u0006\u0002\u0010(J\u0016\u0010P\u001a\u00020!2\u0006\u0010\u0019\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010Q\u001a\u00020!2\u0006\u00107\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u00108J\u0016\u0010R\u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0086@\u00a2\u0006\u0002\u00102J\u0016\u0010S\u001a\u00020\b2\u0006\u00107\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u00108R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\nR\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\nR\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006T"}, d2 = {"Lcom/vibesync/app/data/LibraryRepository;", "", "db", "Lcom/vibesync/app/data/db/AppDatabase;", "(Lcom/vibesync/app/data/db/AppDatabase;)V", "downloadedTracks", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/vibesync/app/data/db/TrackEntity;", "getDownloadedTracks", "()Lkotlinx/coroutines/flow/Flow;", "likedTracks", "getLikedTracks", "playlistDao", "Lcom/vibesync/app/data/db/PlaylistDao;", "playlistTrackDao", "Lcom/vibesync/app/data/db/PlaylistTrackDao;", "playlists", "Lcom/vibesync/app/data/db/PlaylistEntity;", "getPlaylists", "recentPlaylists", "getRecentPlaylists", "trackDao", "Lcom/vibesync/app/data/db/TrackDao;", "addSearchResultToDefault", "result", "Lcom/vibesync/app/extractor/SearchResult;", "(Lcom/vibesync/app/extractor/SearchResult;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addToDefault", "resolved", "Lcom/vibesync/app/extractor/ResolvedAudio;", "(Lcom/vibesync/app/extractor/ResolvedAudio;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addTrackToPlaylist", "", "playlistId", "", "videoId", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearDownload", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createPlaylist", "name", "description", "coverImageUri", "themeColorArgb", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePlaylist", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTrack", "ensureDefaultPlaylist", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ensureTrackSaved", "track", "(Lcom/vibesync/app/data/db/TrackEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDownloadedTracksOnce", "getLikedTracksOnce", "getTrack", "getTracksForPlaylistOnce", "importPlaylist", "tracks", "Lcom/vibesync/app/data/ImportedTrack;", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markDownloaded", "path", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeCoverThumbnail", "observeTrackCount", "observeTracksForPlaylist", "playlistIdsForTrack", "removeTrackFromPlaylist", "renamePlaylist", "reorderPlaylist", "orderedVideoIds", "(JLjava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchTracks", "query", "toggleLike", "toggleLikeFromSearchResult", "toggleLikeTrack", "touchPlaylist", "upsertAndPrependToDefault", "app_debug"})
public final class LibraryRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.TrackDao trackDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.PlaylistDao playlistDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.db.PlaylistTrackDao playlistTrackDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> playlists = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> recentPlaylists = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> downloadedTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> likedTracks = null;
    
    public LibraryRepository(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.AppDatabase db) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> getPlaylists() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.PlaylistEntity>> getRecentPlaylists() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> getDownloadedTracks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> getLikedTracks() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDownloadedTracksOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion) {
        return null;
    }
    
    /**
     * Used by the Now Playing screen's heart/"Add to playlist" actions, which may be acting on
     * a currently-playing track that was never saved (an ad-hoc direct play) -- saves it first
     * (lands in "All songs", like every other save path) if it isn't in the library yet.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object ensureTrackSaved(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion) {
        return null;
    }
    
    /**
     * Same idea as [toggleLikeFromSearchResult] but starting from a [TrackEntity] (the Now
     * Playing screen's heart icon) rather than a [SearchResult].
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleLikeTrack(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLikedTracksOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion) {
        return null;
    }
    
    /**
     * Toggles like state for a track already known to the library (e.g. from a playlist or
     * the Downloads tab).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleLike(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Toggles like for a search/trending result that may not be saved yet -- liking always
     * saves it (lands in "All songs" like everything else does), since "Liked Songs" needs the
     * track to actually exist in the library.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleLikeFromSearchResult(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.SearchResult result, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object ensureDefaultPlaylist(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.PlaylistEntity> $completion) {
        return null;
    }
    
    /**
     * Saves a resolved link as a track and puts it at the front of the Default playlist.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addToDefault(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.ResolvedAudio resolved, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion) {
        return null;
    }
    
    /**
     * Adds a song found via [com.vibesync.app.extractor.YouTubeSearcher] straight from its
     * search-result metadata -- no stream resolve needed since we're only saving it, not
     * playing it yet.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addSearchResultToDefault(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.extractor.SearchResult result, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion) {
        return null;
    }
    
    private final java.lang.Object upsertAndPrependToDefault(com.vibesync.app.data.db.TrackEntity track, kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> observeTracksForPlaylist(long playlistId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Integer> observeTrackCount(long playlistId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> observeCoverThumbnail(long playlistId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object touchPlaylist(long playlistId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTracksForPlaylistOnce(long playlistId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.vibesync.app.data.db.TrackEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object playlistIdsForTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vibesync.app.data.db.TrackEntity>> searchTracks(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object reorderPlaylist(long playlistId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> orderedVideoIds, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createPlaylist(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.String coverImageUri, @org.jetbrains.annotations.Nullable()
    java.lang.Integer themeColorArgb, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.PlaylistEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object renamePlaylist(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deletePlaylist(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addTrackToPlaylist(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeTrackFromPlaylist(long playlistId, @org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Deletes a track everywhere (cascades out of every playlist, including Default).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTrack(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.TrackEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markDownloaded(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Recreates a playlist from an imported link list -- no network calls, since export/import
     * only ever carries metadata + the original YouTube link, never a (short-lived) stream URL.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object importPlaylist(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.ImportedTrack> tracks, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vibesync.app.data.db.PlaylistEntity> $completion) {
        return null;
    }
}