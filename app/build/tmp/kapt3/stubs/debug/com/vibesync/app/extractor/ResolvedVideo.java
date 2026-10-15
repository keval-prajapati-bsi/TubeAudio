package com.vibesync.app.extractor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\f0\tH\u00c6\u0003JS\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\tH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014\u00a8\u0006$"}, d2 = {"Lcom/vibesync/app/extractor/ResolvedVideo;", "", "videoId", "", "title", "thumbnailUrl", "durationSeconds", "", "qualities", "", "Lcom/vibesync/app/extractor/VideoQuality;", "chapters", "Lcom/vibesync/app/extractor/Chapter;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/util/List;Ljava/util/List;)V", "getChapters", "()Ljava/util/List;", "getDurationSeconds", "()J", "getQualities", "getThumbnailUrl", "()Ljava/lang/String;", "getTitle", "getVideoId", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class ResolvedVideo {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String videoId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailUrl = null;
    private final long durationSeconds = 0L;
    
    /**
     * Sorted highest resolution first. Never empty -- [resolve] throws instead of returning a
     * video with nothing playable.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.vibesync.app.extractor.VideoQuality> qualities = null;
    
    /**
     * Same YouTube chapter markers [AudioStreamResolver] already surfaces for audio playback --
     * the video player was missing this entirely even though the underlying data was always
     * available from the same `StreamExtractor`, just never read here. Most videos have none (an
     * empty list is the normal case, not an error).
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.vibesync.app.extractor.Chapter> chapters = null;
    
    public ResolvedVideo(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, long durationSeconds, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.VideoQuality> qualities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.Chapter> chapters) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVideoId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getThumbnailUrl() {
        return null;
    }
    
    public final long getDurationSeconds() {
        return 0L;
    }
    
    /**
     * Sorted highest resolution first. Never empty -- [resolve] throws instead of returning a
     * video with nothing playable.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.VideoQuality> getQualities() {
        return null;
    }
    
    /**
     * Same YouTube chapter markers [AudioStreamResolver] already surfaces for audio playback --
     * the video player was missing this entirely even though the underlying data was always
     * available from the same `StreamExtractor`, just never read here. Most videos have none (an
     * empty list is the normal case, not an error).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.Chapter> getChapters() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.VideoQuality> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.Chapter> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.extractor.ResolvedVideo copy(@org.jetbrains.annotations.NotNull()
    java.lang.String videoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, long durationSeconds, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.VideoQuality> qualities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.Chapter> chapters) {
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