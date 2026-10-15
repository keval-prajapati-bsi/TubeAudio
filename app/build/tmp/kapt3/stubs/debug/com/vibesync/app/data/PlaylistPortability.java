package com.vibesync.app.data;

/**
 * Playlists export/import as a small portable JSON file: links + metadata only,
 * never the resolved stream URL (those expire within hours) or downloaded audio
 * bytes -- keeps files tiny and importing never needs a network call up front.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J$\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004\u00a8\u0006\u000f"}, d2 = {"Lcom/vibesync/app/data/PlaylistPortability;", "", "()V", "exportToShareableUri", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "playlistName", "", "tracks", "", "Lcom/vibesync/app/data/db/TrackEntity;", "importFromUri", "Lcom/vibesync/app/data/ImportedPlaylist;", "uri", "app_debug"})
public final class PlaylistPortability {
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.data.PlaylistPortability INSTANCE = null;
    
    private PlaylistPortability() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.net.Uri exportToShareableUri(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String playlistName, @org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.data.db.TrackEntity> tracks) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.vibesync.app.data.ImportedPlaylist importFromUri(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
        return null;
    }
}