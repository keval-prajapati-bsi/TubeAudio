package com.vibesync.app.playback;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b#\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u00ab\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u0012\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014\u00a2\u0006\u0002\u0010\u0016J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\rH\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u00c6\u0003J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u00103\u001a\u00020\u0007H\u00c6\u0003J\t\u00104\u001a\u00020\rH\u00c6\u0003J\u00af\u0001\u00105\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00032\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u00c6\u0001J\u0013\u00106\u001a\u00020\u00032\b\u00107\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00108\u001a\u000209H\u00d6\u0001J\t\u0010:\u001a\u00020\u0007H\u00d6\u0001R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u001eR\u0011\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u001eR\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u001eR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001aR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001cR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001cR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001cR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001cR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001c\u00a8\u0006;"}, d2 = {"Lcom/vibesync/app/playback/PlaybackUiState;", "", "isPlaying", "", "isBuffering", "needsResolveToResume", "videoId", "", "title", "thumbnailUrl", "url", "queueLabel", "positionMs", "", "durationMs", "hasNext", "isShuffleEnabled", "isRepeatEnabled", "errorMessage", "chapters", "", "Lcom/vibesync/app/extractor/Chapter;", "(ZZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJZZZLjava/lang/String;Ljava/util/List;)V", "getChapters", "()Ljava/util/List;", "getDurationMs", "()J", "getErrorMessage", "()Ljava/lang/String;", "getHasNext", "()Z", "getNeedsResolveToResume", "getPositionMs", "getQueueLabel", "getThumbnailUrl", "getTitle", "getUrl", "getVideoId", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class PlaybackUiState {
    private final boolean isPlaying = false;
    private final boolean isBuffering = false;
    
    /**
     * True right after restoring the last-played track on app launch: shown as a
     * paused placeholder until the user presses play, since the stream URL has
     * to be re-resolved (the previous one has long since expired).
     */
    private final boolean needsResolveToResume = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String videoId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailUrl = null;
    
    /**
     * The original YouTube watch link (never the resolved stream URL) -- used by the Now
     * Playing screen's "Copy link" action.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String url = null;
    
    /**
     * What queue is currently playing ("All songs", "Downloads", "Liked Songs", a custom
     * playlist's name...), shown as "Playing from {queueLabel}" on the expanded Now Playing
     * screen. Empty when not meaningful (e.g. an ad-hoc direct play).
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String queueLabel = null;
    private final long positionMs = 0L;
    private final long durationMs = 0L;
    
    /**
     * True only once the next track is genuinely queued in the player (not just
     * "there logically is a next track") -- see PlayerController's class doc.
     */
    private final boolean hasNext = false;
    private final boolean isShuffleEnabled = false;
    private final boolean isRepeatEnabled = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String errorMessage = null;
    
    /**
     * YouTube's own chapter markers for the current track, if it has any (most don't -- an
     * empty list is normal). Populated once stream resolution finishes, cleared the moment a
     * new track starts loading so the old track's chapters don't briefly show against it.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.vibesync.app.extractor.Chapter> chapters = null;
    
    public PlaybackUiState(boolean isPlaying, boolean isBuffering, boolean needsResolveToResume, @org.jetbrains.annotations.Nullable()
    java.lang.String videoId, @org.jetbrains.annotations.Nullable()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    java.lang.String queueLabel, long positionMs, long durationMs, boolean hasNext, boolean isShuffleEnabled, boolean isRepeatEnabled, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.Chapter> chapters) {
        super();
    }
    
    public final boolean isPlaying() {
        return false;
    }
    
    public final boolean isBuffering() {
        return false;
    }
    
    /**
     * True right after restoring the last-played track on app launch: shown as a
     * paused placeholder until the user presses play, since the stream URL has
     * to be re-resolved (the previous one has long since expired).
     */
    public final boolean getNeedsResolveToResume() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getVideoId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getThumbnailUrl() {
        return null;
    }
    
    /**
     * The original YouTube watch link (never the resolved stream URL) -- used by the Now
     * Playing screen's "Copy link" action.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getUrl() {
        return null;
    }
    
    /**
     * What queue is currently playing ("All songs", "Downloads", "Liked Songs", a custom
     * playlist's name...), shown as "Playing from {queueLabel}" on the expanded Now Playing
     * screen. Empty when not meaningful (e.g. an ad-hoc direct play).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getQueueLabel() {
        return null;
    }
    
    public final long getPositionMs() {
        return 0L;
    }
    
    public final long getDurationMs() {
        return 0L;
    }
    
    /**
     * True only once the next track is genuinely queued in the player (not just
     * "there logically is a next track") -- see PlayerController's class doc.
     */
    public final boolean getHasNext() {
        return false;
    }
    
    public final boolean isShuffleEnabled() {
        return false;
    }
    
    public final boolean isRepeatEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    /**
     * YouTube's own chapter markers for the current track, if it has any (most don't -- an
     * empty list is normal). Populated once stream resolution finishes, cleared the moment a
     * new track starts loading so the old track's chapters don't briefly show against it.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.Chapter> getChapters() {
        return null;
    }
    
    public PlaybackUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final long component10() {
        return 0L;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component13() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.extractor.Chapter> component15() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    public final boolean component3() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.playback.PlaybackUiState copy(boolean isPlaying, boolean isBuffering, boolean needsResolveToResume, @org.jetbrains.annotations.Nullable()
    java.lang.String videoId, @org.jetbrains.annotations.Nullable()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    java.lang.String queueLabel, long positionMs, long durationMs, boolean hasNext, boolean isShuffleEnabled, boolean isRepeatEnabled, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
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