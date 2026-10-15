package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u00a6\u0001\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00070\u00062\u0018\u0010\b\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\t\u001a\u00020\n2P\u0010\u000b\u001aL\u0012\u0013\u0012\u0011H\u0002\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\u0010\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\u00010\f\u00a2\u0006\u0002\b\u0013H\u0007\u00a8\u0006\u0014"}, d2 = {"DragReorderColumn", "", "T", "items", "", "key", "Lkotlin/Function1;", "", "onOrderChanged", "modifier", "Landroidx/compose/ui/Modifier;", "rowContent", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "item", "", "isDragging", "dragModifier", "Landroidx/compose/runtime/Composable;", "app_debug"})
public final class DragReorderColumnKt {
    
    /**
     * Generic drag-to-reorder list, shared by the playlist detail screen's custom-order view and the
     * Queue screen's "Up Next" list. A plain (non-lazy) [Column], same reasoning as before: item
     * count here is personal-app scale, and a `LazyColumn`'s recycling makes drag-position math
     * meaningfully harder to get right.
     *
     * The dragged row is moved directly via touch (unchanged from the original approach). What's new
     * is a FLIP-style animation ("First, Last, Invert, Play") for every *other* row whose position
     * shifts because of that drag: when [localOrder] mutates, each item that moved is snapped
     * backward by exactly the pixel distance it just jumped (so it visually stays put for one frame)
     * and then animated forward to zero over ~220ms. Without this, a plain reordering list only
     * animates the row you're actually touching -- everything else instantly snaps to its new slot,
     * which is what read as the list "getting stuck on top of another song": the item you dragged
     * past would jump-cut into its new position instead of sliding out of the way.
     */
    @androidx.compose.runtime.Composable()
    public static final <T extends java.lang.Object>void DragReorderColumn(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends T> items, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super T, ? extends java.lang.Object> key, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<? extends T>, kotlin.Unit> onOrderChanged, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super T, ? super java.lang.Boolean, ? super androidx.compose.ui.Modifier, kotlin.Unit> rowContent) {
    }
}