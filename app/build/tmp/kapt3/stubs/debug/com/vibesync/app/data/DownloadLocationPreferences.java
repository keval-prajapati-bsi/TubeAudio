package com.vibesync.app.data;

/**
 * Null (the default) means downloads go to app-private storage, same as always. A non-null
 * value is a persisted SAF tree URI string (from [android.content.Intent.ACTION_OPEN_DOCUMENT_TREE]
 * via `ActivityResultContracts.OpenDocumentTree`) the user picked in Profile -> Download
 * location -- only applies to downloads started *after* it's set, existing downloads stay put.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/vibesync/app/data/DownloadLocationPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_customTreeUri", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "customTreeUri", "Lkotlinx/coroutines/flow/StateFlow;", "getCustomTreeUri", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "setCustomTreeUri", "", "uriString", "Companion", "app_debug"})
public final class DownloadLocationPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _customTreeUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> customTreeUri = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_TREE_URI = "download_tree_uri";
    @org.jetbrains.annotations.NotNull()
    private static final com.vibesync.app.data.DownloadLocationPreferences.Companion Companion = null;
    
    public DownloadLocationPreferences(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCustomTreeUri() {
        return null;
    }
    
    public final void setCustomTreeUri(@org.jetbrains.annotations.Nullable()
    java.lang.String uriString) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/vibesync/app/data/DownloadLocationPreferences$Companion;", "", "()V", "KEY_TREE_URI", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}