package com.vibesync.app.ui.theme;

/**
 * Design-system spacing/corner tokens. Corner radii are pulled from the actual Figma file's fill
 * data (frequency count across all 31 screens' nodes), not eyeballed -- 16dp is the dominant card
 * radius by a wide margin (66 uses), 100dp is the file's own convention for fully-round pills/
 * avatars. Spacing keeps the 8dp-grid scale from the earlier typed design brief, since the Figma
 * file's actual node positions are consistent with (though don't literally encode) that same grid.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001e\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0019\u0010\u0003\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0005\u0010\u0006R\u0019\u0010\b\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\t\u0010\u0006R\u0019\u0010\n\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u000b\u0010\u0006R\u0019\u0010\f\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\r\u0010\u0006R\u0019\u0010\u000e\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u000f\u0010\u0006R\u0019\u0010\u0010\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0011\u0010\u0006R\u0019\u0010\u0012\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0013\u0010\u0006R\u0019\u0010\u0014\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0015\u0010\u0006R\u0019\u0010\u0016\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0017\u0010\u0006R\u0019\u0010\u0018\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0019\u0010\u0006R\u0019\u0010\u001a\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u001b\u0010\u0006R\u0019\u0010\u001c\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u001d\u0010\u0006R\u0019\u0010\u001e\u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u001f\u0010\u0006R\u0019\u0010 \u001a\u00020\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b!\u0010\u0006\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u0006\""}, d2 = {"Lcom/vibesync/app/ui/theme/Dimens;", "", "()V", "CardPadding", "Landroidx/compose/ui/unit/Dp;", "getCardPadding-D9Ej5fM", "()F", "F", "CornerAlbumArt", "getCornerAlbumArt-D9Ej5fM", "CornerBottomNav", "getCornerBottomNav-D9Ej5fM", "CornerButton", "getCornerButton-D9Ej5fM", "CornerCard", "getCornerCard-D9Ej5fM", "CornerImage", "getCornerImage-D9Ej5fM", "CornerPill", "getCornerPill-D9Ej5fM", "CornerSearchBar", "getCornerSearchBar-D9Ej5fM", "LargeGap", "getLargeGap-D9Ej5fM", "ListItemPadding", "getListItemPadding-D9Ej5fM", "MediumGap", "getMediumGap-D9Ej5fM", "OuterMargin", "getOuterMargin-D9Ej5fM", "SectionGap", "getSectionGap-D9Ej5fM", "SmallGap", "getSmallGap-D9Ej5fM", "app_debug"})
public final class Dimens {
    private static final float OuterMargin = 0.0F;
    private static final float SectionGap = 0.0F;
    private static final float CardPadding = 0.0F;
    private static final float ListItemPadding = 0.0F;
    private static final float SmallGap = 0.0F;
    private static final float MediumGap = 0.0F;
    private static final float LargeGap = 0.0F;
    private static final float CornerCard = 0.0F;
    private static final float CornerButton = 0.0F;
    private static final float CornerImage = 0.0F;
    private static final float CornerSearchBar = 0.0F;
    private static final float CornerBottomNav = 0.0F;
    private static final float CornerAlbumArt = 0.0F;
    private static final float CornerPill = 0.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.vibesync.app.ui.theme.Dimens INSTANCE = null;
    
    private Dimens() {
        super();
    }
}