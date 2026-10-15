package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001aI\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u0015\b\u0002\u0010\u000b\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\n\u00a2\u0006\u0002\b\fH\u0003\u001a\u001e\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a$\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u0015H\u0003\u00a8\u0006\u0016"}, d2 = {"EmptyQueueMessage", "", "text", "", "QueueRow", "title", "thumbnailUrl", "isCurrent", "", "onClick", "Lkotlin/Function0;", "trailing", "Landroidx/compose/runtime/Composable;", "QueueScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onBack", "QueueTabSelector", "selected", "Lcom/vibesync/app/ui/QueueTab;", "onSelect", "Lkotlin/Function1;", "app_debug"})
public final class QueueScreenKt {
    
    /**
     * "Up Next" -- everything queued after the currently-playing track, drag-reorderable; "History"
     * -- songs actually played, most recent first. Matches the reference mockup's two-tab layout.
     */
    @androidx.compose.runtime.Composable()
    public static final void QueueScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QueueTabSelector(com.vibesync.app.ui.QueueTab selected, kotlin.jvm.functions.Function1<? super com.vibesync.app.ui.QueueTab, kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyQueueMessage(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QueueRow(java.lang.String title, java.lang.String thumbnailUrl, boolean isCurrent, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> trailing) {
    }
}