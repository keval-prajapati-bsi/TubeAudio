package com.vibesync.app.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.extractor.SearchResult
import com.vibesync.app.ui.MainViewModel
import kotlinx.coroutines.launch

/** Shared between the Search and Trending tabs: a list of YouTube results, each with a "+"
 *  that adds the song (always lands in "All songs") and immediately opens the "Add to
 *  playlist" picker so the user can also drop it into a specific playlist right away. */
@Composable
fun SearchResultsList(
    results: List<SearchResult>,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    /** Passed by the Podcasts tab only -- see [YouTubeSearchResultRow]'s own doc on why this is
     *  podcast-specific rather than a general row action. */
    onWatchVideo: ((SearchResult) -> Unit)? = null
) {
    val addedVideoIds by viewModel.addedVideoIds.collectAsStateWithLifecycle()
    val likedVideoIds by viewModel.likedVideoIds.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.queuedVideoIds.collectAsStateWithLifecycle()
    var addToPlaylistTrack by remember { mutableStateOf<TrackEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = modifier) {
        items(results, key = { it.videoId }) { result ->
            YouTubeSearchResultRow(
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
                onWatchVideo = onWatchVideo?.let { { it(result) } }
            )
        }
    }

    addToPlaylistTrack?.let { track ->
        AddToPlaylistDialog(
            track = track,
            playlists = playlists,
            viewModel = viewModel,
            onDismiss = { addToPlaylistTrack = null }
        )
    }
}
