package com.vibesync.app.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.extractor.TrendingFilter
import com.vibesync.app.extractor.TrendingFilters
import com.vibesync.app.ui.components.SearchResultsList
import com.vibesync.app.ui.theme.HeroGradient
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun TrendingScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val category by viewModel.trendingCategory.collectAsStateWithLifecycle()
    val showAllCategories by viewModel.showAllCategories.collectAsStateWithLifecycle()

    BackHandler(enabled = category != null || showAllCategories) {
        if (category != null) viewModel.closeTrendingCategory() else viewModel.closeAllCategories()
    }

    when {
        category != null -> TrendingCategoryScreen(viewModel = viewModel, filter = category!!, modifier = modifier)
        showAllCategories -> TrendingAllCategoriesScreen(viewModel = viewModel, modifier = modifier)
        else -> TrendingMainScreen(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
private fun TrendingMainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val results by viewModel.trendingResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingTrending.collectAsStateWithLifecycle()
    val addedVideoIds by viewModel.addedVideoIds.collectAsStateWithLifecycle()
    val likedVideoIds by viewModel.likedVideoIds.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    var addToPlaylistTrack by remember { mutableStateOf<com.vibesync.app.data.db.TrackEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.loadTrendingIfNeeded() }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text(
                text = "Trending now",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            TrendingHeroCard(
                filter = TrendingFilters.first(),
                onClick = { viewModel.openTrendingCategory(TrendingFilters.first()) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top charts",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { viewModel.openAllCategories() }) {
                    Text("See all", color = NavyPrimary)
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(TrendingFilters) { filter ->
                    ChartCard(
                        filter = filter,
                        onClick = { viewModel.openTrendingCategory(filter) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending songs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (results.isNotEmpty()) {
                    com.vibesync.app.ui.components.RoundPlayButton(onClick = { viewModel.playTrendingResultsFromStart() })
                }
            }
        }

        when {
            isLoading && results.isEmpty() -> item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            results.isEmpty() -> item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Couldn't load trending songs right now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                items(results, key = { it.videoId }) { result ->
                    com.vibesync.app.ui.components.YouTubeSearchResultRow(
                        result = result,
                        isAdded = addedVideoIds.contains(result.videoId),
                        isLiked = likedVideoIds.contains(result.videoId),
                        isCurrentlyPlaying = playbackState.isPlaying && playbackState.videoId == result.videoId,
                        isInQueue = queuedVideoIds.contains(result.videoId),
                        onPlay = { viewModel.playSearchResultDirectly(result) },
                        onToggleLike = { viewModel.toggleLikeSearchResult(result) },
                        onAddToQueue = { viewModel.addSearchResultToQueue(result) },
                        onAdd = {
                            coroutineScope.launch {
                                val track = viewModel.addSearchResultAndReturnTrack(result)
                                if (track != null) addToPlaylistTrack = track
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    addToPlaylistTrack?.let { track ->
        com.vibesync.app.ui.components.AddToPlaylistDialog(
            track = track,
            playlists = playlists,
            viewModel = viewModel,
            onDismiss = { addToPlaylistTrack = null }
        )
    }
}

@Composable
private fun TrendingCategoryScreen(viewModel: MainViewModel, filter: TrendingFilter, modifier: Modifier = Modifier) {
    val results by viewModel.categoryResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingCategory.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.closeTrendingCategory() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = filter.label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (results.isNotEmpty()) {
                com.vibesync.app.ui.components.RoundPlayButton(onClick = { viewModel.playCategoryResultsFromStart() })
            }
        }

        when {
            isLoading && results.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            results.isEmpty() -> Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Couldn't load \"${filter.label}\" right now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            else -> SearchResultsList(results = results, viewModel = viewModel, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun TrendingAllCategoriesScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.closeAllCategories() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Top charts", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(TrendingFilters) { filter ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.closeAllCategories()
                            viewModel.openTrendingCategory(filter)
                        }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(HeroGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Album, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendingHeroCard(filter: TrendingFilter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(144.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp), ambientColor = HeroGradient[0], spotColor = HeroGradient[1])
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(HeroGradient))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "TRENDING RIGHT NOW",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = SurfaceWhite.copy(alpha = 0.85f)
            )
            Text(
                text = filter.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = SurfaceWhite
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Album, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun ChartCard(filter: TrendingFilter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(130.dp)
            .aspectRatio(1.1f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp), ambientColor = HeroGradient[0])
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(HeroGradient))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        com.vibesync.app.ui.components.VinylDisk(modifier = Modifier.align(Alignment.TopEnd), size = 30.dp)
        Text(
            text = filter.label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = SurfaceWhite,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}
