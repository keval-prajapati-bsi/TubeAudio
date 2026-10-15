package com.vibesync.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// Design-system re-theme sourced from the actual VibeSync Figma file (31 screens), pulled via the
// Figma REST API and reverse-engineered from real fill/stroke frequency counts across the whole
// document -- not eyeballed, not a typed spec, the literal pixel data. Dominant palette: Zinc
// neutrals + a violet-600 accent (Tailwind's own default scale, the convention the Figma file
// itself was built on). Notably #7C3AED is the exact color this file's *previous* violet identity
// already used before the lime/orchid detour -- the Figma design effectively confirms violet was
// right, just paired with a true Zinc-neutral dark scale instead of the custom near-black/pink one.
// Light theme has no reference in the Figma file (every screen shown is dark), so it keeps its
// prior near-white family rather than inventing unreviewed light values.
// Same "module-level mutableStateOf vars, flipped by applyThemeColors()" pattern established
// since v7 -- every existing call site reads these inside a @Composable, so this file alone is
// what needs to change for the whole app to re-theme; no per-screen migration needed.
private val LightNavyPrimary = Color(0xFF7C3AED)
private val DarkNavyPrimary = Color(0xFF7C3AED) // figma: violet-600, 105 fill + 74 stroke uses -- the dominant accent
private val LightNavyPrimaryContainer = Color(0xFFEDE9FE)
private val DarkNavyPrimaryContainer = Color(0xFF27272A) // figma: zinc-800, the file's one raised/bordered tone
private val LightAccentTeal = Color(0xFF9333EA)
private val DarkAccentTeal = Color(0xFF8B5CF6) // figma: violet-500, a lighter tint of the same accent family
private val LightBackgroundTint = Color(0xFFF4F1F8)
private val DarkBackgroundTint = Color(0xFF27272A) // figma: zinc-800 (surfaceVariant / nested-element fill)
private val LightCardBorder = Color(0xFFE8E1F0)
private val DarkCardBorder = Color(0xFF27272A) // figma: zinc-800, 126 stroke uses -- the file's actual border color
private val LightTextPrimary = Color(0xFF17121C)
private val DarkTextPrimary = Color(0xFFFAFAFA) // figma: zinc-50, 333 uses -- the dominant text color
private val LightTextSecondary = Color(0xFF746C7E)
private val DarkTextSecondary = Color(0xFFA1A1AA) // figma: zinc-400, 249 uses -- the dominant secondary text
private val LightErrorRed = Color(0xFFDE3163)
private val DarkErrorRed = Color(0xFFEF4444) // figma: red-500
private val LightDivider = Color(0xFFEDEBF5)
private val DarkDivider = Color(0xFF27272A) // figma: zinc-800
private val LightPageBackground = Color(0xFFFAFAFD)
private val DarkPageBackground = Color(0xFF09090B) // figma: zinc-950, 59 uses -- the actual page background
private val LightSurfaceContainer = Color(0xFFFFFFFF)
private val DarkSurfaceContainer = Color(0xFF18181B) // figma: zinc-900, 121 uses -- the actual card/surface tone

/** A raised-above-surface layer (floating nav, mini player). The Figma file's fill data only
 *  supports three real dark tones (page/surface/border), not a distinct fourth "elevated" step, so
 *  this reuses the border tone (zinc-800) rather than fabricating an unfounded shade. */
private val LightElevatedCard = Color(0xFFFFFFFF)
private val DarkElevatedCard = Color(0xFF27272A)
var ElevatedCardBackground by mutableStateOf(LightElevatedCard)
    private set

/** figma: emerald-500, used for online-status dots (Friends list) -- a real semantic color found in
 *  the file, not part of the accent system. Fixed, not theme-reactive, matching [ErrorRed]'s own
 *  "this specific meaning always reads this way" precedent. */
val SuccessGreen = Color(0xFF10B981)

/** Hero/highlight card gradient (Dashboard's featured playlist, Trending's hero card,
 *  CreatePlaylistDialog's default cover) -- a monochrome violet-600-to-violet-400 blend, staying
 *  inside the Figma file's own accent family rather than reaching for an unrelated hue. Appears
 *  the same in both themes. */
val HeroGradient = listOf(Color(0xFF7C3AED), Color(0xFFA78BFA))

var NavyPrimary by mutableStateOf(LightNavyPrimary)
    private set
var NavyPrimaryDark by mutableStateOf(LightNavyPrimaryContainer)
    private set
var AccentTeal by mutableStateOf(LightAccentTeal)
    private set

/** Deliberately theme-invariant: used as icon/content color on top of a saturated-but-DARK
 *  accent tile (e.g. a white play icon on the [HeroGradient] violet), which reads fine regardless
 *  of light/dark mode. NOT safe on the primary/secondary accent themselves (both are light --
 *  lime #D7ED4A, orchid #D98AF6) -- use [OnAccent] there instead. See that doc for why this split
 *  exists now. */
val SurfaceWhite = Color(0xFFFFFFFF)

/** Content color for anything sitting directly on [NavyPrimary]/[AccentTeal] (a filled button, the
 *  play/pause circle, a selected filter chip) -- both accents are light, high-luminance colors
 *  (lime, orchid), so white content on top of them fails contrast; this reuses the page background
 *  itself for a deliberate near-black-on-lime look instead. Wired into [MaterialTheme]'s
 *  `onPrimary`/`onSecondary` in `Theme.kt` so default Material3 components (Button, FAB, Switch)
 *  get this for free -- only hand-styled custom composables that hardcode `tint = SurfaceWhite` on
 *  an accent-filled background need fixing individually (grep `background(NavyPrimary)` /
 *  `background(AccentTeal)` if a future report is "icon isn't visible" on an accent-colored
 *  control -- same bug class as the recurring ambient-`LocalContentColor` one, different cause). */
var OnAccent by mutableStateOf(Color(0xFF17121C))
    private set

/** The floating mini player's own background -- deliberately a single fixed color, NOT theme-
 *  reactive, so the capsule looks the same in light and dark mode rather than following the page
 *  behind it. Reuses the *light* theme's values specifically (frozen, not swapped by
 *  [applyThemeColors]) -- carried over from the v16 fix that established this direction was the
 *  one that actually read well; recolored to the new palette but the same choice of which theme
 *  to freeze. */
val PlayerCapsuleBackground = Color(0xFFF3F1FA)
val PlayerCapsuleContentPrimary = Color(0xFF16141F)
val PlayerCapsuleContentSecondary = Color(0xFF847F94)
val PlayerCapsuleSurfaceVariant = Color(0xFFE7E2F5)

var BackgroundWhite by mutableStateOf(LightBackgroundTint)
    private set
var CardBorder by mutableStateOf(LightCardBorder)
    private set
var TextPrimary by mutableStateOf(LightTextPrimary)
    private set
var TextSecondary by mutableStateOf(LightTextSecondary)
    private set
var ErrorRed by mutableStateOf(LightErrorRed)
    private set
var DividerColor by mutableStateOf(LightDivider)
    private set
var PageBackground by mutableStateOf(LightPageBackground)
    private set
var SurfaceContainer by mutableStateOf(LightSurfaceContainer)
    private set

/** [accentOverride] is the user's "Accent color" swatch pick (null = this theme's own default);
 *  [secondaryOverride] only ever comes paired with a "More themes" two-tone gradient preset --
 *  a lone accent pick leaves the secondary/[AccentTeal] color at its normal themed value. */
internal fun applyThemeColors(isDark: Boolean, accentOverride: Color? = null, secondaryOverride: Color? = null) {
    NavyPrimary = accentOverride ?: if (isDark) DarkNavyPrimary else LightNavyPrimary
    NavyPrimaryDark = if (isDark) DarkNavyPrimaryContainer else LightNavyPrimaryContainer
    AccentTeal = secondaryOverride ?: (if (isDark) DarkAccentTeal else LightAccentTeal)
    BackgroundWhite = if (isDark) DarkBackgroundTint else LightBackgroundTint
    ElevatedCardBackground = if (isDark) DarkElevatedCard else LightElevatedCard
    CardBorder = if (isDark) DarkCardBorder else LightCardBorder
    TextPrimary = if (isDark) DarkTextPrimary else LightTextPrimary
    TextSecondary = if (isDark) DarkTextSecondary else LightTextSecondary
    ErrorRed = if (isDark) DarkErrorRed else LightErrorRed
    DividerColor = if (isDark) DarkDivider else LightDivider
    PageBackground = if (isDark) DarkPageBackground else LightPageBackground
    SurfaceContainer = if (isDark) DarkSurfaceContainer else LightSurfaceContainer
    // Accent overrides (user-picked "Accent color" swatch) can be dark, so this can't just be a
    // fixed per-theme constant -- pick readable content color relative to the ACTUAL accent in use.
    OnAccent = if (NavyPrimary.luminance() > 0.5f) Color(0xFF17121C) else Color(0xFFFFFFFF)
}
