package com.vibesync.app.extractor;

/**
 * Resolves a video's actual *playable-at-every-resolution* streams -- separate from
 * [AudioStreamResolver], which only ever wants this app's normal case, the single best
 * audio-only stream for background listening. Must only be called off the main thread (same
 * blocking-network/JS-eval caveat as [AudioStreamResolver.resolve]).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u001e\u0010\u0007\u001a\u00020\b*\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0006H\u0002\u00a8\u0006\r"}, d2 = {"Lcom/vibesync/app/extractor/VideoStreamResolver;", "", "()V", "resolve", "Lcom/vibesync/app/extractor/ResolvedVideo;", "rawUrl", "", "toQuality", "Lcom/vibesync/app/extractor/VideoQuality;", "Lorg/schabi/newpipe/extractor/stream/VideoStream;", "isMuxed", "", "audioUrl", "app_debug"})
public final class VideoStreamResolver {
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.extractor.VideoStreamResolver INSTANCE = null;
    
    private VideoStreamResolver() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.extractor.ResolvedVideo resolve(@org.jetbrains.annotations.NotNull()
    java.lang.String rawUrl) {
        return null;
    }
    
    private final com.vibesync.app.extractor.VideoQuality toQuality(org.schabi.newpipe.extractor.stream.VideoStream $this$toQuality, boolean isMuxed, java.lang.String audioUrl) {
        return null;
    }
}