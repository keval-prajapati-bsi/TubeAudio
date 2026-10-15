package com.vibesync.app.data.localparty;

/**
 * The host's single source of truth, broadcast in full to every connected guest on every change
 * (see [com.vibesync.app.localparty.LocalPartyHost]) -- unlike the Firebase party's Firestore
 * documents (many small targeted writes), a raw socket has no partial-update/diff concept of its
 * own, so sending the whole snapshot each time is the simplest correct approach at LAN-party
 * scale (a handful of phones, a short queue).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b \n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\b\u0018\u0000 52\u00020\u0001:\u00015B\u0085\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u0013J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u000eH\u00c6\u0003J\t\u0010$\u001a\u00020\u000eH\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u00c6\u0003J\u000b\u0010\'\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u000eH\u00c6\u0003J\t\u0010,\u001a\u00020\u0010H\u00c6\u0003J\u008b\u0001\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u000e2\b\b\u0002\u0010\u0012\u001a\u00020\u000eH\u00c6\u0001J\u0013\u0010.\u001a\u00020\u00102\b\u0010/\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00100\u001a\u000201H\u00d6\u0001J\u0006\u00102\u001a\u000203J\t\u00104\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0017R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u001bR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0012\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001dR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0017R\u0011\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015\u00a8\u00066"}, d2 = {"Lcom/vibesync/app/data/localparty/LocalRoomState;", "", "roomName", "", "members", "", "Lcom/vibesync/app/data/localparty/LocalMember;", "queue", "Lcom/vibesync/app/data/localparty/LocalQueueItem;", "currentVideoId", "currentTitle", "currentThumbnailUrl", "currentUrl", "currentDurationSeconds", "", "isPlaying", "", "startPositionMs", "playbackStartedAtMillis", "(Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZJJ)V", "getCurrentDurationSeconds", "()J", "getCurrentThumbnailUrl", "()Ljava/lang/String;", "getCurrentTitle", "getCurrentUrl", "getCurrentVideoId", "()Z", "getMembers", "()Ljava/util/List;", "getPlaybackStartedAtMillis", "getQueue", "getRoomName", "getStartPositionMs", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toJson", "Lorg/json/JSONObject;", "toString", "Companion", "app_debug"})
public final class LocalRoomState {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String roomName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.vibesync.app.data.localparty.LocalMember> members = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> queue = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentVideoId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentTitle = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentThumbnailUrl = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String currentUrl = null;
    private final long currentDurationSeconds = 0L;
    private final boolean isPlaying = false;
    
    /**
     * Same expected-position formula as the Firebase party (see `PartySyncController`):
     * `startPositionMs + (now - playbackStartedAtMillis)` while playing. Both ends of the socket
     * share the same wall-clock assumption the Firebase version already relies on (no clock-skew
     * handshake) -- same disclosed simplification, not new to the local room.
     */
    private final long startPositionMs = 0L;
    private final long playbackStartedAtMillis = 0L;
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.data.localparty.LocalRoomState.Companion Companion = null;
    
    public LocalRoomState(@org.jetbrains.annotations.NotNull()
    java.lang.String roomName, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.localparty.LocalMember> members, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> queue, @org.jetbrains.annotations.Nullable()
    java.lang.String currentVideoId, @org.jetbrains.annotations.Nullable()
    java.lang.String currentTitle, @org.jetbrains.annotations.Nullable()
    java.lang.String currentThumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUrl, long currentDurationSeconds, boolean isPlaying, long startPositionMs, long playbackStartedAtMillis) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoomName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.data.localparty.LocalMember> getMembers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> getQueue() {
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
    
    public final boolean isPlaying() {
        return false;
    }
    
    /**
     * Same expected-position formula as the Firebase party (see `PartySyncController`):
     * `startPositionMs + (now - playbackStartedAtMillis)` while playing. Both ends of the socket
     * share the same wall-clock assumption the Firebase version already relies on (no clock-skew
     * handshake) -- same disclosed simplification, not new to the local room.
     */
    public final long getStartPositionMs() {
        return 0L;
    }
    
    public final long getPlaybackStartedAtMillis() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.json.JSONObject toJson() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final long component10() {
        return 0L;
    }
    
    public final long component11() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.data.localparty.LocalMember> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> component3() {
        return null;
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
    
    public final long component8() {
        return 0L;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.localparty.LocalRoomState copy(@org.jetbrains.annotations.NotNull()
    java.lang.String roomName, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.localparty.LocalMember> members, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.localparty.LocalQueueItem> queue, @org.jetbrains.annotations.Nullable()
    java.lang.String currentVideoId, @org.jetbrains.annotations.Nullable()
    java.lang.String currentTitle, @org.jetbrains.annotations.Nullable()
    java.lang.String currentThumbnailUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUrl, long currentDurationSeconds, boolean isPlaying, long startPositionMs, long playbackStartedAtMillis) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/vibesync/app/data/localparty/LocalRoomState$Companion;", "", "()V", "fromJson", "Lcom/vibesync/app/data/localparty/LocalRoomState;", "json", "Lorg/json/JSONObject;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.vibesync.app.data.localparty.LocalRoomState fromJson(@org.jetbrains.annotations.NotNull()
        org.json.JSONObject json) {
            return null;
        }
    }
}