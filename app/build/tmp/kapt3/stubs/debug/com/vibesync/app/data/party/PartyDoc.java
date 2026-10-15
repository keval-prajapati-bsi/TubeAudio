package com.vibesync.app.data.party;

/**
 * The party's live state: which track is "now playing" and exactly when it started, so any
 * client (including one joining mid-song) can compute the correct current position without the
 * host having to broadcast a position update every second. See [com.vibesync.app.playback.PartySyncController]
 * for how [playbackStartedAtMillis]/[startPositionMs] turn into an actual seek position.
 *
 * Deliberately hand-mapped from a [DocumentSnapshot] rather than `toObject()`, since
 * [playbackStartedAtMillis]/[createdAtMillis] are written as Firestore server [com.google.firebase.Timestamp]
 * values but read back as plain millis here -- `toObject()`'s reflection-based mapping doesn't
 * reconcile that type difference and would throw at read time.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b&\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 82\u00020\u0001:\u00018B\u007f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\f\u0012\u0006\u0010\u0011\u001a\u00020\f\u0012\u0006\u0010\u0012\u001a\u00020\f\u00a2\u0006\u0002\u0010\u0013J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u000fH\u00c6\u0003J\t\u0010\'\u001a\u00020\fH\u00c6\u0003J\t\u0010(\u001a\u00020\fH\u00c6\u0003J\t\u0010)\u001a\u00020\fH\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010.\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\fH\u00c6\u0003J\u009f\u0001\u00102\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\f2\b\b\u0002\u0010\u0011\u001a\u00020\f2\b\b\u0002\u0010\u0012\u001a\u00020\fH\u00c6\u0001J\u0013\u00103\u001a\u00020\u000f2\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u000206H\u00d6\u0001J\t\u00107\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0012\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0015R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0015R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0015R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010 R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015R\u0011\u0010\u0011\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0017R\u0011\u0010\u0010\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0017\u00a8\u00069"}, d2 = {"Lcom/vibesync/app/data/party/PartyDoc;", "", "code", "", "hostUid", "hostName", "name", "currentVideoId", "currentTitle", "currentThumbnailUrl", "currentUrl", "currentDurationSeconds", "", "currentQueueItemId", "isPlaying", "", "startPositionMs", "playbackStartedAtMillis", "createdAtMillis", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;ZJJJ)V", "getCode", "()Ljava/lang/String;", "getCreatedAtMillis", "()J", "getCurrentDurationSeconds", "getCurrentQueueItemId", "getCurrentThumbnailUrl", "getCurrentTitle", "getCurrentUrl", "getCurrentVideoId", "getHostName", "getHostUid", "()Z", "getName", "getPlaybackStartedAtMillis", "getStartPositionMs", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "app_debug"})
public final class PartyDoc {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String hostUid = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String hostName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentVideoId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentTitle = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentThumbnailUrl = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentUrl = null;
    private final long currentDurationSeconds = 0L;
    
    /**
     * The queue item (if any) that [currentVideoId] corresponds to -- null for an ad-hoc play
     * never added to the queue. This is what lets "skip next"/"go back" know which queue item to
     * transition, and lets the UI tell "now playing" apart from "up next" without a 4th status.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentQueueItemId = null;
    private final boolean isPlaying = false;
    
    /**
     * The track position, in ms, at the instant [playbackStartedAtMillis] was written.
     */
    private final long startPositionMs = 0L;
    private final long playbackStartedAtMillis = 0L;
    private final long createdAtMillis = 0L;
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.data.party.PartyDoc.Companion Companion = null;
    
    public PartyDoc(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String hostUid, @org.jetbrains.annotations.NotNull()
    java.lang.String hostName, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String currentVideoId, @org.jetbrains.annotations.Nullable()
    java.lang.String currentTitle, @org.jetbrains.annotations.Nullable()
    java.lang.String currentThumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUrl, long currentDurationSeconds, @org.jetbrains.annotations.Nullable()
    java.lang.String currentQueueItemId, boolean isPlaying, long startPositionMs, long playbackStartedAtMillis, long createdAtMillis) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHostUid() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHostName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentVideoId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentThumbnailUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUrl() {
        return null;
    }
    
    public final long getCurrentDurationSeconds() {
        return 0L;
    }
    
    /**
     * The queue item (if any) that [currentVideoId] corresponds to -- null for an ad-hoc play
     * never added to the queue. This is what lets "skip next"/"go back" know which queue item to
     * transition, and lets the UI tell "now playing" apart from "up next" without a 4th status.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentQueueItemId() {
        return null;
    }
    
    public final boolean isPlaying() {
        return false;
    }
    
    /**
     * The track position, in ms, at the instant [playbackStartedAtMillis] was written.
     */
    public final long getStartPositionMs() {
        return 0L;
    }
    
    public final long getPlaybackStartedAtMillis() {
        return 0L;
    }
    
    public final long getCreatedAtMillis() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final long component12() {
        return 0L;
    }
    
    public final long component13() {
        return 0L;
    }
    
    public final long component14() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.party.PartyDoc copy(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    java.lang.String hostUid, @org.jetbrains.annotations.NotNull()
    java.lang.String hostName, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String currentVideoId, @org.jetbrains.annotations.Nullable()
    java.lang.String currentTitle, @org.jetbrains.annotations.Nullable()
    java.lang.String currentThumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUrl, long currentDurationSeconds, @org.jetbrains.annotations.Nullable()
    java.lang.String currentQueueItemId, boolean isPlaying, long startPositionMs, long playbackStartedAtMillis, long createdAtMillis) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/vibesync/app/data/party/PartyDoc$Companion;", "", "()V", "fromSnapshot", "Lcom/vibesync/app/data/party/PartyDoc;", "doc", "Lcom/google/firebase/firestore/DocumentSnapshot;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.vibesync.app.data.party.PartyDoc fromSnapshot(@org.jetbrains.annotations.NotNull()
        com.google.firebase.firestore.DocumentSnapshot doc) {
            return null;
        }
    }
}