package com.vibesync.app.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.ui.components.ConfirmDialog
import com.vibesync.app.ui.components.TextPromptDialog
import com.vibesync.app.ui.components.VinylDisk
import com.vibesync.app.ui.components.playlistTileColor
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.HeroGradient
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import java.util.Calendar

private const val DASHBOARD_PLAYLIST_LIMIT = 5

/** Season detection for the Home page's opening shelf. Deliberately just the device's own
 *  calendar month, no location permission or weather API -- same "approximate, don't fake a real
 *  data source" honesty as [SearchScreen]'s genre tiles and Trending's charts. The four buckets
 *  below are the Northern-hemisphere / North Indian calendar (this app's own content skews
 *  Bollywood/Hindi), so this would need a real hemisphere or locale check before it'd make sense
 *  for a Southern-hemisphere user -- flagged here, not solved, since that's a bigger call than a
 *  Home-screen shelf should make silently. */
private enum class Season { WINTER, SUMMER, MONSOON, POST_MONSOON }

private fun currentSeason(): Season = when (Calendar.getInstance().get(Calendar.MONTH)) {
    Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> Season.WINTER
    Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Season.SUMMER
    Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER -> Season.MONSOON
    else -> Season.POST_MONSOON
}

private fun seasonEyebrow(season: Season): String = when (season) {
    Season.WINTER -> "Detected near you · Winter"
    Season.SUMMER -> "Detected near you · Summer"
    Season.MONSOON -> "Detected near you · Monsoon"
    Season.POST_MONSOON -> "Detected near you · Post-monsoon"
}

private fun seasonTitle(season: Season): String = when (season) {
    Season.WINTER -> "Cozy Winter Nights"
    Season.SUMMER -> "Summer Anthems"
    Season.MONSOON -> "Rain Therapy"
    Season.POST_MONSOON -> "Golden Hour"
}

/** Each card runs a representative search via [MainViewModel.searchFor], the exact same
 *  mechanism [SearchScreen]'s BrowseGenreTile already uses -- no separate "curated season
 *  playlist" backend, same honest approximation. */
private data class SeasonMood(val label: String, val subtitle: String, val query: String, val icon: ImageVector, val gradient: List<Color>)

private fun seasonMoods(season: Season): List<SeasonMood> = when (season) {
    Season.MONSOON -> listOf(
        SeasonMood("Rain Therapy", "Rain sounds", "rain sounds relaxing songs", Icons.Filled.WaterDrop, listOf(Color(0xFF2F5F73), Color(0xFF12262F))),
        SeasonMood("Thunder & Chill", "Ambient", "thunderstorm ambient chill songs", Icons.Filled.Thunderstorm, listOf(Color(0xFF3A4F73), Color(0xFF141C2F))),
        SeasonMood("Petrichor Mix", "Monsoon hits", "monsoon hindi songs", Icons.Filled.Park, listOf(Color(0xFF4A5F4A), Color(0xFF1C2B1C)))
    )
    Season.WINTER -> listOf(
        SeasonMood("Snowfall Ambience", "Instrumental", "winter cozy instrumental songs", Icons.Filled.AcUnit, listOf(Color(0xFF3A4A73), Color(0xFF141C33))),
        SeasonMood("Fireside Acoustic", "Unplugged", "acoustic unplugged winter songs", Icons.Filled.LocalFireDepartment, listOf(Color(0xFF734A3A), Color(0xFF331C14)))
    )
    Season.SUMMER -> listOf(
        SeasonMood("Summer Road Trip", "Upbeat", "summer road trip upbeat songs", Icons.Filled.WbSunny, listOf(Color(0xFF735F2F), Color(0xFF2F2612))),
        SeasonMood("Beach Party", "Dance", "summer party dance songs", Icons.Filled.Celebration, listOf(Color(0xFF2F6373), Color(0xFF122A2F)))
    )
    Season.POST_MONSOON -> listOf(
        SeasonMood("Golden Hour", "Evening chill", "evening chill hindi songs", Icons.Filled.Park, listOf(Color(0xFF735F3A), Color(0xFF332614))),
        SeasonMood("Cozy Evenings", "Soft acoustic", "night acoustic soft songs", Icons.Filled.NightsStay, listOf(Color(0xFF3B3050), Color(0xFF181329)))
    )
}

/** Top-of-Home mood filter chips. Deselected by default; tapping one runs the same "representative
 *  search, not a real curated category" mechanism as the season cards and [SearchScreen]'s genre
 *  tiles -- tapping the already-selected chip deselects it again rather than being a permanent
 *  pick. */
private data class HomeFilterChip(val label: String, val query: String)

private val HomeFilterChips = listOf(
    HomeFilterChip("Podcasts", "podcast episodes"),
    HomeFilterChip("Relax", "relaxing chill songs"),
    HomeFilterChip("Romance", "romantic love songs"),
    HomeFilterChip("Energise", "energetic workout songs")
)

@Composable
private fun HomeFilterChipsRow(selectedLabel: String?, onSelect: (HomeFilterChip) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(HomeFilterChips, key = { it.label }) { chip ->
            val isSelected = chip.label == selectedLabel
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else BackgroundWhite)
                    .clickable { onSelect(chip) }
                    .padding(horizontal = 16.dp, vertical = 9.dp)
            ) {
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                    color = if (isSelected) com.vibesync.app.ui.theme.OnAccent else TextSecondary
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val recentPlaylists by viewModel.recentPlaylists.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val profileName by viewModel.profileName.collectAsStateWithLifecycle()
    val recommendedResults by viewModel.recommendedResults.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var renameTarget by remember { mutableStateOf<PlaylistEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<PlaylistEntity?>(null) }
    var selectedFilterChip by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(isOnline) { if (isOnline) viewModel.loadRecommendedIfNeeded() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = com.vibesync.app.ui.theme.Dimens.OuterMargin)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(HeroGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Album,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(11.dp)
                )
            }
            Text(
                text = "VibeSync",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f).padding(start = 10.dp)
            )
            IconButton(onClick = { viewModel.openNotifications() }) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = { viewModel.selectTab(AppTab.SEARCH) }) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface)
            }
            val avatarHaptics = androidx.compose.ui.platform.LocalHapticFeedback.current
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(HeroGradient))
                    .clickable {
                        avatarHaptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.openProfile()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profileName.take(1).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SurfaceWhite
                )
            }
        }

        Box(modifier = Modifier.padding(top = 12.dp)) {
            HomeFilterChipsRow(
                selectedLabel = selectedFilterChip,
                onSelect = { chip ->
                    selectedFilterChip = if (selectedFilterChip == chip.label) null else chip.label
                    if (selectedFilterChip != null) {
                        viewModel.searchFor(chip.query)
                        viewModel.selectTab(AppTab.SEARCH)
                    }
                }
            )
        }

        val season = remember { currentSeason() }
        SeasonMoodShelf(
            season = season,
            moods = remember(season) { seasonMoods(season) },
            onMoodClick = { mood ->
                viewModel.searchFor(mood.query)
                viewModel.selectTab(AppTab.SEARCH)
            }
        )

        SectionHeader(title = "Recently Played", onSeeAll = { viewModel.selectTab(AppTab.LIBRARY) })

        if (recentPlaylists.isEmpty()) {
            Text(
                text = "Play something and it'll show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(recentPlaylists, key = { it.id }) { playlist ->
                    RecentlyPlayedTile(
                        playlist = playlist,
                        viewModel = viewModel,
                        onClick = { viewModel.playPlaylistFromStart(playlist.id) }
                    )
                }
            }
        }

        if (!isOnline) {
            SectionHeader(title = "Recommended for you", onSeeAll = { viewModel.selectTab(AppTab.DOWNLOADS) }, seeAllLabel = "Downloads")
            OfflineCard(onGoToDownloads = { viewModel.selectTab(AppTab.DOWNLOADS) })
        } else if (recommendedResults.isNotEmpty()) {
            // A horizontal shelf of square cover-art cards, not a vertical numbered list -- the
            // one visual pattern Spotify/YouTube Music/Apple Music/JioSaavn's Home screens all
            // share for exactly this kind of "here's a stack of individual songs" section, and
            // the previous plain list was the one part of Home that didn't match that shelf
            // language every other section here already used (Recently Played, Your Playlists).
            SectionHeader(title = "Recommended for you", onSeeAll = { viewModel.openFullRecommendedSearch() })
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(recommendedResults.take(10), key = { it.videoId }) { result ->
                    HomeShelfCard(
                        title = result.title,
                        subtitle = result.uploaderName ?: "",
                        onClick = { viewModel.playSearchResultDirectly(result) }
                    ) {
                        if (result.thumbnailUrl != null) {
                            coil.compose.AsyncImage(
                                model = result.thumbnailUrl,
                                contentDescription = result.title,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Filled.Album, contentDescription = null, tint = TextSecondary)
                        }
                    }
                }
            }
        }

        SectionHeader(title = "Your Playlists", onSeeAll = { viewModel.selectTab(AppTab.LIBRARY) })

        if (playlists.isEmpty()) {
            Text(
                text = "Your playlists will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(playlists.take(DASHBOARD_PLAYLIST_LIMIT), key = { it.id }) { playlist ->
                    val count by remember(playlist.id) { viewModel.trackCount(playlist.id) }.collectAsStateWithLifecycle(0)
                    val tileColor = playlistTileColor(playlist)
                    Box {
                        HomeShelfCard(
                            title = playlist.name,
                            subtitle = if (count == 1) "1 song" else "$count songs",
                            onClick = {
                                viewModel.openPlaylist(playlist.id)
                                viewModel.selectTab(AppTab.LIBRARY)
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(tileColor), contentAlignment = Alignment.Center) {
                                VinylDisk(size = 32.dp)
                            }
                        }
                        if (!playlist.isDefault) {
                            var menuOpen by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                                    Icon(
                                        Icons.Filled.MoreVert,
                                        contentDescription = "More",
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                androidx.compose.material3.DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Export") },
                                        onClick = {
                                            menuOpen = false
                                            viewModel.exportPlaylist(playlist.id, playlist.name) { uri ->
                                                context.startActivity(
                                                    Intent(Intent.ACTION_SEND).apply {
                                                        type = "application/json"
                                                        putExtra(Intent.EXTRA_STREAM, uri)
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                )
                                            }
                                        }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Rename") },
                                        onClick = { menuOpen = false; renameTarget = playlist }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = { menuOpen = false; deleteTarget = playlist }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(96.dp))
    }

    renameTarget?.let { playlist ->
        TextPromptDialog(
            title = "Rename playlist",
            label = "Playlist name",
            confirmLabel = "Save",
            initialValue = playlist.name,
            onDismiss = { renameTarget = null },
            onConfirm = { name -> viewModel.renamePlaylist(playlist.id, name) }
        )
    }

    deleteTarget?.let { playlist ->
        ConfirmDialog(
            title = "Delete \"${playlist.name}\"?",
            message = "This only removes the playlist. The songs stay in your library.",
            onConfirm = { viewModel.deletePlaylist(playlist.id) },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit, seeAllLabel: String = "See All") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = com.vibesync.app.ui.theme.Dimens.SectionGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onSeeAll) {
            Text(seeAllLabel, color = NavyPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
    }
}

/** The one section on Home that's a row of cards side-by-side, never a vertical list -- per
 *  explicit direction, since this reads as "a handful of moods to pick from" rather than "a
 *  queue of items to scan", the same distinction the mood/season cards in the reference mockup
 *  drew against Quick Picks' list-style rows. */
@Composable
private fun SeasonMoodShelf(season: Season, moods: List<SeasonMood>, onMoodClick: (SeasonMood) -> Unit) {
    Text(
        text = seasonEyebrow(season),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(top = 18.dp)
    )
    Text(
        text = seasonTitle(season),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 4.dp)
    ) {
        items(moods, key = { it.label }) { mood ->
            SeasonMoodCard(mood = mood, onClick = { onMoodClick(mood) })
        }
    }
}

@Composable
private fun SeasonMoodCard(mood: SeasonMood, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(178.dp)
            .height(140.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(22.dp), ambientColor = mood.gradient[0], spotColor = mood.gradient[0])
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(mood.gradient))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = mood.icon, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(
                text = mood.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = SurfaceWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = mood.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SurfaceWhite.copy(alpha = 0.8f)
            )
        }
    }
}

private val HomeCardWidth = 128.dp

/** Shared card shape for every Home shelf below the season cards -- Recently Played, Recommended
 *  for you, and Your Playlists all render through this one composable now, at the same fixed
 *  width, so the three sections read as one consistent card language instead of three different
 *  treatments (a flat-color tile row, a cover-art row, and a plain list). */
@Composable
private fun HomeShelfCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    art: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .width(HomeCardWidth)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerImage))
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            art()
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OfflineCard(onGoToDownloads: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = "You're offline",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Recommendations need an internet connection, but your downloaded songs still work offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )
        TextButton(onClick = onGoToDownloads) {
            Text("Go to Downloads", color = NavyPrimary)
        }
    }
}

/** A flat colored tile (playlist's own theme color) with a small centered vinyl icon, rendered
 *  through the shared [HomeShelfCard] so it's the same size as every other Home shelf card. */
@Composable
private fun RecentlyPlayedTile(playlist: PlaylistEntity, viewModel: MainViewModel, onClick: () -> Unit) {
    val count by remember(playlist.id) { viewModel.trackCount(playlist.id) }.collectAsStateWithLifecycle(0)
    val tileColor = playlistTileColor(playlist)
    HomeShelfCard(
        title = playlist.name,
        subtitle = if (count == 1) "1 song" else "$count songs",
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize().background(tileColor), contentAlignment = Alignment.Center) {
            VinylDisk(size = 32.dp)
        }
    }
}
