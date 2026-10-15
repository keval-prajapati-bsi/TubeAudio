package com.vibesync.app.ui.theme

import androidx.compose.ui.unit.dp

/** Design-system spacing/corner tokens. Corner radii are pulled from the actual Figma file's fill
 *  data (frequency count across all 31 screens' nodes), not eyeballed -- 16dp is the dominant card
 *  radius by a wide margin (66 uses), 100dp is the file's own convention for fully-round pills/
 *  avatars. Spacing keeps the 8dp-grid scale from the earlier typed design brief, since the Figma
 *  file's actual node positions are consistent with (though don't literally encode) that same grid. */
object Dimens {
    val OuterMargin = 20.dp
    val SectionGap = 24.dp
    val CardPadding = 18.dp
    val ListItemPadding = 16.dp
    val SmallGap = 8.dp
    val MediumGap = 12.dp
    val LargeGap = 24.dp

    val CornerCard = 16.dp
    val CornerButton = 12.dp
    val CornerImage = 12.dp
    val CornerSearchBar = 16.dp
    val CornerBottomNav = 20.dp
    val CornerAlbumArt = 24.dp
    val CornerPill = 100.dp
}
