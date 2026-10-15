package com.vibesync.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Hierarchy per the reference spec: large headlines Bold, section titles SemiBold, song title
// SemiBold, artist name Medium, captions Regular. headlineSmall/titleMedium/titleSmall/bodyMedium
// map to those four roles respectively -- titleSmall and bodyMedium specifically are what most
// TrackRow/card composables already reach for as "song title" and "artist name" style, so bumping
// their weights here (Medium->SemiBold, Normal->Medium) carries the new hierarchy through the app
// without per-call-site changes. bodySmall/labelMedium are left at Material3's own Regular
// defaults for true captions/timestamps, matching the spec's lightest tier.
val TubeAudioTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp)
)
