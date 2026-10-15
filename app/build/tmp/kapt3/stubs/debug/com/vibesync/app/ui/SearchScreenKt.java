package com.vibesync.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u001e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\tH\u0003\u001a(\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00022\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\b\b\u0002\u0010\f\u001a\u00020\rH\u0003\u001a:\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00042\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00060\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\u0006\u0010\u0013\u001a\u00020\u0014H\u0003\u001a\b\u0010\u0015\u001a\u00020\u0006H\u0003\u001a,\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00060\tH\u0003\u001a\u001a\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a:\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00042\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00060\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\u0006\u0010\u001d\u001a\u00020\u0014H\u0003\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00040\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"BrowseGenres", "", "Lcom/vibesync/app/ui/BrowseGenre;", "PopularIndianArtists", "", "ArtistChip", "", "name", "onClick", "Lkotlin/Function0;", "BrowseGenreTile", "genre", "modifier", "Landroidx/compose/ui/Modifier;", "PasteLinkCard", "value", "onValueChange", "Lkotlin/Function1;", "onSubmit", "isResolving", "", "PasteLinkCardPreview", "RecentSearchRow", "query", "onRemove", "SearchScreen", "viewModel", "Lcom/vibesync/app/ui/MainViewModel;", "SearchSongCard", "isSearching", "app_debug"})
public final class SearchScreenKt {
    
    /**
     * Curated, not a live chart -- NewPipeExtractor has no "trending artists" data source, so this
     * is a fixed list of well-known popular Indian artists; tapping one just searches their name.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> PopularIndianArtists = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.vibesync.app.ui.BrowseGenre> BrowseGenres = null;
    
    @androidx.compose.runtime.Composable()
    public static final void SearchScreen(@org.jetbrains.annotations.NotNull()
    com.vibesync.app.ui.MainViewModel viewModel, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RecentSearchRow(java.lang.String query, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> onRemove) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ArtistChip(java.lang.String name, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * A single flat-color genre tile with a large rotated icon bleeding off the corner -- the same
     * "bold color block + oversized decorative glyph" language Spotify's Browse grid uses, adapted
     * to this app's rounder/softer corner radius rather than copied pixel-for-pixel.
     */
    @androidx.compose.runtime.Composable()
    private static final void BrowseGenreTile(com.vibesync.app.ui.BrowseGenre genre, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PasteLinkCard(java.lang.String value, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onValueChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSubmit, boolean isResolving) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchSongCard(java.lang.String value, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onValueChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSubmit, boolean isSearching) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true)
    @androidx.compose.runtime.Composable()
    private static final void PasteLinkCardPreview() {
    }
}