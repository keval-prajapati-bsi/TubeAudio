package com.vibesync.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.ui.components.SearchResultsList
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary

/** Reached via the bottom nav's Menu -> Podcasts entry. Defaults to a "latest podcasts" listing
 *  (a canned search, sorted newest-first -- see [com.vibesync.app.extractor.YouTubeSearcher.podcastSearch]
 *  for why there's no real "podcasts" category API to pull a true chart from), with its own search
 *  box for finding a specific show/episode. Plays through the exact same [SearchResultsList] /
 *  [MainViewModel] playback path as every other screen -- podcast episodes are audio-only tracks
 *  like anything else in this app, so chapter markers (see `NowPlayingScreen`'s `ChapterRow`) work
 *  identically here without any podcast-specific playback code. */
@Composable
fun PodcastScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val query by viewModel.podcastQuery.collectAsStateWithLifecycle()
    val results by viewModel.podcastResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingPodcasts.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadLatestPodcastsIfNeeded() }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Podcasts",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onPodcastQueryChanged,
            placeholder = { Text("Search podcasts") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.submitPodcastSearch() }),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = NavyPrimary
            )
        )

        if (query.isBlank()) {
            Text(
                text = "Latest episodes, newest first",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        if (isLoading && results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No podcasts found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            SearchResultsList(
                results = results,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                onWatchVideo = { result -> viewModel.openVideoPlayer(result.url, result.title) }
            )
        }
    }
}
