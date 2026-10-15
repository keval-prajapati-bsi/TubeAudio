package com.vibesync.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a@\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0016\b\u0002\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0001\u0018\u00010\nH\u0007\u00a8\u0006\u000b"}, d2 = {"SearchResultsList", "", "results", "", "Lcom/vibesync/app/extractor/SearchResult;", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "modifier", "Landroidx/compose/ui/Modifier;", "onWatchVideo", "Lkotlin/Function1;", "app_debug"})
public final class SearchResultsListKt {
    
    /**
     * Shared between the Search and Trending tabs: a list of YouTube results, each with a "+"
     * that adds the song (always lands in "All songs") and immediately opens the "Add to
     * playlist" picker so the user can also drop it into a specific playlist right away.
     */
    @androidx.compose.runtime.Composable()
    public static final void SearchResultsList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.vibesync.app.extractor.SearchResult> results, @org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.vibesync.app.extractor.SearchResult, kotlin.Unit> onWatchVideo) {
    }
}