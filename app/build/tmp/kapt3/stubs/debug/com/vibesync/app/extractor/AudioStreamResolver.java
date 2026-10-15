package com.vibesync.app.extractor;

/**
 * Resolves a pasted YouTube link into a direct, playable audio-only stream URL
 * via NewPipeExtractor. Must only be called off the main thread -- it performs
 * blocking network calls and, for ciphered streams, JS evaluation.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/vibesync/app/extractor/AudioStreamResolver;", "", "()V", "bareIdPattern", "Lkotlin/text/Regex;", "videoIdPattern", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "extractVideoId", "", "rawUrl", "resolve", "Lcom/vibesync/app/extractor/ResolvedAudio;", "app_debug"})
public final class AudioStreamResolver {
    private static final java.util.regex.Pattern videoIdPattern = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex bareIdPattern = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.extractor.AudioStreamResolver INSTANCE = null;
    
    private AudioStreamResolver() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String extractVideoId(@org.jetbrains.annotations.NotNull()
    java.lang.String rawUrl) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.extractor.ResolvedAudio resolve(@org.jetbrains.annotations.NotNull()
    java.lang.String rawUrl) {
        return null;
    }
}