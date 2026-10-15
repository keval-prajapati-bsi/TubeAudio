package com.vibesync.app.download;

/**
 * Downloads a track's resolved audio stream so it can be played back with no network at all.
 * By default files live in [Context.getFilesDir] (app-private storage, gone on uninstall); if
 * the user has picked a custom folder via [DownloadLocationPreferences] (Profile -> Download
 * location, a Storage Access Framework tree), new downloads go there instead as a
 * `content://...` file, and [TrackEntity.localFilePath] carries whichever kind of path/URI is
 * actually in use -- everything downstream (playback, deletion) branches on whether that string
 * starts with `content://`.
 *
 * Supports pausing (cancels the job but remembers bytes written and the resolved stream URL, for
 * an HTTP Range-resume within the same app session -- a fresh process restart falls back to
 * starting that track over). Byte-range resume only applies to the default app-private location;
 * a custom SAF folder always restarts a resumed download from scratch, since document providers
 * don't reliably support append-writes the same way a local file does -- a known simplification,
 * same spirit as the existing "pause doesn't survive process death" one.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000e\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\u000eJ\u0010\u0010)\u001a\u00020\'2\u0006\u0010(\u001a\u00020\u000eH\u0002J\u0006\u0010*\u001a\u00020\'J\u001c\u0010+\u001a\u00020\'2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u000f0-H\u0086@\u00a2\u0006\u0002\u0010.J\u000e\u0010/\u001a\u00020\'2\u0006\u00100\u001a\u00020\u000fJ\u0010\u00101\u001a\u00020\u000e2\u0006\u00102\u001a\u00020\u000eH\u0002J2\u00103\u001a\u0014\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u000205\u0012\u0004\u0012\u00020\u0019042\u0006\u0010(\u001a\u00020\u000e2\u0006\u00102\u001a\u00020\u000e2\u0006\u00106\u001a\u00020\u0019H\u0002J\u0019\u00107\u001a\u0004\u0018\u00010\u00192\b\u00108\u001a\u0004\u0018\u00010\u000eH\u0002\u00a2\u0006\u0002\u00109J\u000e\u0010:\u001a\u00020\'2\u0006\u0010(\u001a\u00020\u000eJ\u0016\u0010;\u001a\u00020\'2\u0006\u00100\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010<J\u000e\u0010=\u001a\u00020\'2\u0006\u00100\u001a\u00020\u000fJ\u0018\u0010>\u001a\u00020\'2\u0006\u00100\u001a\u00020\u000f2\u0006\u00106\u001a\u00020\u0019H\u0002R \u0010\u000b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0011\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00120\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\u0013\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00190\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001c\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\u001e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00120\r0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020!0\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020#0\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006?"}, d2 = {"Lcom/vibesync/app/download/TrackDownloader;", "", "context", "Landroid/content/Context;", "httpClient", "Lokhttp3/OkHttpClient;", "libraryRepository", "Lcom/vibesync/app/data/LibraryRepository;", "downloadLocationPreferences", "Lcom/vibesync/app/data/DownloadLocationPreferences;", "(Landroid/content/Context;Lokhttp3/OkHttpClient;Lcom/vibesync/app/data/LibraryRepository;Lcom/vibesync/app/data/DownloadLocationPreferences;)V", "_activeDownloadTracks", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "", "Lcom/vibesync/app/data/db/TrackEntity;", "_downloadErrors", "_downloadStates", "Lcom/vibesync/app/download/DownloadState;", "activeDownloadTracks", "Lkotlinx/coroutines/flow/StateFlow;", "getActiveDownloadTracks", "()Lkotlinx/coroutines/flow/StateFlow;", "bytesWritten", "", "", "defaultDownloadDir", "Ljava/io/File;", "downloadErrors", "getDownloadErrors", "downloadStates", "getDownloadStates", "jobs", "Lkotlinx/coroutines/Job;", "resolvedAudio", "Lcom/vibesync/app/extractor/ResolvedAudio;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "cancelDownload", "", "videoId", "deleteAnyExistingOutput", "dismissError", "downloadPlaylist", "tracks", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadTrack", "track", "mimeTypeForExtension", "extension", "openDownloadTarget", "Lkotlin/Triple;", "Ljava/io/OutputStream;", "resumeFromBytes", "parseTotalFromContentRange", "header", "(Ljava/lang/String;)Ljava/lang/Long;", "pauseDownload", "removeDownload", "(Lcom/vibesync/app/data/db/TrackEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resumeDownload", "startDownloadJob", "app_debug"})
public final class TrackDownloader {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient httpClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.LibraryRepository libraryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vibesync.app.data.DownloadLocationPreferences downloadLocationPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final java.io.File defaultDownloadDir = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, kotlinx.coroutines.Job> jobs = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Long> bytesWritten = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.vibesync.app.extractor.ResolvedAudio> resolvedAudio = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, com.vibesync.app.download.DownloadState>> _downloadStates = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.vibesync.app.download.DownloadState>> downloadStates = null;
    
    /**
     * Tracks currently downloading or paused, keyed by videoId -- lets the Downloads tab show
     * an in-progress row (with its live progress ring) before the track is actually marked
     * downloaded in the database.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, com.vibesync.app.data.db.TrackEntity>> _activeDownloadTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.vibesync.app.data.db.TrackEntity>> activeDownloadTracks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _downloadErrors = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> downloadErrors = null;
    
    public TrackDownloader(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient httpClient, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.LibraryRepository libraryRepository, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.DownloadLocationPreferences downloadLocationPreferences) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.vibesync.app.download.DownloadState>> getDownloadStates() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, com.vibesync.app.data.db.TrackEntity>> getActiveDownloadTracks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getDownloadErrors() {
        return null;
    }
    
    public final void downloadTrack(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track) {
    }
    
    public final void pauseDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    public final void resumeDownload(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track) {
    }
    
    public final void cancelDownload(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId) {
    }
    
    /**
     * Sequential on purpose (one track at a time) to avoid hammering bandwidth/YouTube with
     * many parallel resolves; waits for each track's job to finish (or pause) before starting
     * the next.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object downloadPlaylist(@org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> tracks, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeDownload(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.data.db.TrackEntity track, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void dismissError() {
    }
    
    /**
     * Deletes whatever's on disk for [videoId], regardless of extension -- covers both a
     * same-session partial download (known extension) and any stale file left over from before
     * downloads used the stream's real format extension (was always ".audio").
     */
    private final void deleteAnyExistingOutput(java.lang.String videoId) {
    }
    
    /**
     * Opens (creating if needed) the output target for [videoId], honoring a custom download
     * location if one is set, and named with [extension] -- the audio stream's *real* container
     * format (e.g. "m4a", "webm"; YouTube never actually serves true MP3 audio-only streams, so
     * this is what makes the saved file playable by other apps instead of a misleading/generic
     * extension). Returns the identifier to persist as [TrackEntity.localFilePath] alongside the
     * open [OutputStream] to write into. [resumeFromBytes] is only honored for the default
     * app-private location -- a custom SAF folder always starts fresh (see class doc).
     */
    private final kotlin.Triple<java.lang.String, java.io.OutputStream, java.lang.Long> openDownloadTarget(java.lang.String videoId, java.lang.String extension, long resumeFromBytes) {
        return null;
    }
    
    private final java.lang.String mimeTypeForExtension(java.lang.String extension) {
        return null;
    }
    
    private final void startDownloadJob(com.vibesync.app.data.db.TrackEntity track, long resumeFromBytes) {
    }
    
    private final java.lang.Long parseTotalFromContentRange(java.lang.String header) {
        return null;
    }
}