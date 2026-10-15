package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a2\u0010\b\u001a\u00020\u00012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010\u000fH\u0003\u001a\u001e\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u00a8\u0006\u0014"}, d2 = {"BottomIconAction", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "", "onClick", "Lkotlin/Function0;", "ChapterRow", "chapters", "", "Lcom/vibesync/app/extractor/Chapter;", "positionMs", "", "onJumpTo", "Lkotlin/Function1;", "NowPlayingScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "onClose", "app_debug"})
public final class NowPlayingScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void NowPlayingScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    /**
     * YouTube's own chapter markers ("sections between videos" you can jump between) -- a horizontal
     * row of tappable chips rather than ticks on the slider itself, since Compose's `Slider` has no
     * built-in way to draw extra marks on its track without a fully custom implementation; this reads
     * just as clearly and is simpler to get right. The chapter containing the current position is
     * highlighted so it's obvious which section is currently playing.
     */
    @androidx.compose.runtime.Composable()
    private static final void ChapterRow(java.util.List<com.vibesync.app.extractor.Chapter> chapters, long positionMs, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onJumpTo) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BottomIconAction(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}