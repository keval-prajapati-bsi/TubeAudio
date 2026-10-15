package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.ui.components.SearchResultsList
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import com.vibesync.app.ui.theme.TubeAudioTheme

/** Curated, not a live chart -- NewPipeExtractor has no "trending artists" data source, so this
 *  is a fixed list of well-known popular Indian artists; tapping one just searches their name. */
private val PopularIndianArtists = listOf(
    "Arijit Singh", "Shreya Ghoshal", "Diljit Dosanjh", "Neha Kakkar",
    "Armaan Malik", "Jubin Nautiyal", "Atif Aslam", "Badshah",
    "Sonu Nigam", "A.R. Rahman"
)

/** Spotify's "Browse" grid -- a wall of bold, single-color genre/mood tiles as the default way to
 *  discover something without typing a specific name -- is one of the most recognizable patterns
 *  shared across Spotify/YouTube Music/Apple Music/JioSaavn's search tabs, and this app's search
 *  page didn't have anything like it (just a text field). Each tile just runs a representative
 *  query, the same "no real category API, approximate with a search" honesty this app already
 *  applies to Trending/Popular artists -- not pretending these are curated charts. */
private data class BrowseGenre(val label: String, val query: String, val color: Color, val icon: ImageVector)

private val BrowseGenres = listOf(
    BrowseGenre("Bollywood", "bollywood hit songs", Color(0xFF6C4CF0), Icons.Filled.Album),
    BrowseGenre("Party", "party dance songs", Color(0xFFE23F8E), Icons.Filled.Celebration),
    BrowseGenre("Romantic", "romantic love songs", Color(0xFFDE3163), Icons.Filled.Favorite),
    BrowseGenre("Hip-Hop", "hip hop rap songs", Color(0xFF2E7D5B), Icons.Filled.Bolt),
    BrowseGenre("Devotional", "devotional bhakti songs", Color(0xFFB8860B), Icons.Filled.LocalFlorist),
    BrowseGenre("Workout", "workout gym songs", Color(0xFF1F7A8C), Icons.Filled.FitnessCenter),
    BrowseGenre("Chill", "chill lofi relaxing songs", Color(0xFF4A5FD9), Icons.Filled.SelfImprovement),
    BrowseGenre("Late Night", "late night acoustic songs", Color(0xFF3B3050), Icons.Filled.NightsStay)
)

@Composable
fun SearchScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val linkInput by viewModel.linkInput.collectAsStateWithLifecycle()
    val isResolving by viewModel.isResolving.collectAsStateWithLifecycle()
    val youtubeQuery by viewModel.youtubeSearchQuery.collectAsStateWithLifecycle()
    val youtubeResults by viewModel.youtubeSearchResults.collectAsStateWithLifecycle()
    val isSearchingYoutube by viewModel.isSearchingYoutube.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )

        PasteLinkCard(
            value = linkInput,
            onValueChange = viewModel::onLinkInputChanged,
            onSubmit = viewModel::submitPastedLink,
            isResolving = isResolving
        )

        SearchSongCard(
            value = youtubeQuery,
            onValueChange = viewModel::onYoutubeSearchQueryChanged,
            onSubmit = viewModel::submitYoutubeSearch,
            isSearching = isSearchingYoutube
        )

        if (youtubeResults.isEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)
            ) {
                if (recentSearches.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent searches",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(recentSearches, key = { it }) { query ->
                        RecentSearchRow(
                            query = query,
                            onClick = { viewModel.searchFor(query) },
                            onRemove = { viewModel.removeRecentSearch(query) }
                        )
                    }
                }

                item {
                    Text(
                        text = "Popular artists",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(PopularIndianArtists, key = { it }) { artist ->
                            ArtistChip(name = artist, onClick = { viewModel.searchFor(artist) })
                        }
                    }
                }

                item {
                    Text(
                        text = "Browse all",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }
                items(BrowseGenres.chunked(2)) { rowGenres ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowGenres.forEach { genre ->
                            BrowseGenreTile(
                                genre = genre,
                                onClick = { viewModel.searchFor(genre.query) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowGenres.size == 1) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search results",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                com.vibesync.app.ui.components.RoundPlayButton(onClick = { viewModel.playSearchResultsFromStart() })
            }
            SearchResultsList(
                results = youtubeResults,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RecentSearchRow(query: String, onClick: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.History, contentDescription = null, tint = TextSecondary)
        Text(
            text = query,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = TextSecondary)
        }
    }
}

@Composable
private fun ArtistChip(name: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(NavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = SurfaceWhite)
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 6.dp)
                .size(width = 72.dp, height = 20.dp)
        )
    }
}

/** A single flat-color genre tile with a large rotated icon bleeding off the corner -- the same
 *  "bold color block + oversized decorative glyph" language Spotify's Browse grid uses, adapted
 *  to this app's rounder/softer corner radius rather than copied pixel-for-pixel. */
@Composable
private fun BrowseGenreTile(genre: BrowseGenre, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(16.dp))
            .background(genre.color)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = genre.icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.28f),
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 4.dp, end = 4.dp)
                .graphicsLayer { rotationZ = -18f }
        )
        Text(
            text = genre.label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun PasteLinkCard(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isResolving: Boolean
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Paste a YouTube link") },
            leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { onSubmit() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = NavyPrimary
            )
        )

        Button(
            onClick = onSubmit,
            enabled = value.isNotBlank() && !isResolving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            if (isResolving) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Play audio")
            }
        }
    }
}

@Composable
private fun SearchSongCard(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSearching: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        placeholder = { Text("Or search for a song by name") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onSubmit, enabled = value.isNotBlank()) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = NavyPrimary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PasteLinkCardPreview() {
    TubeAudioTheme {
        PasteLinkCard(value = "", onValueChange = {}, onSubmit = {}, isResolving = false)
    }
}
