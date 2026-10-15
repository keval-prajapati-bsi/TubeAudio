package com.vibesync.app.extractor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2 = {"USER_AGENT", "", "app_debug"})
public final class OkHttpDownloaderKt {
    
    /**
     * Shared with [com.vibesync.app.download.TrackDownloader] and the ExoPlayer HTTP data source
     * in `PlaybackService` -- YouTube's CDN (googlevideo.com) can 403 requests for a resolved
     * stream URL when the User-Agent doesn't look like a real browser/app, so every raw HTTP
     * request against a resolved URL (streaming playback, downloading) needs to match this one.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36";
}