package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vibesync.app.ui.components.DragReorderColumn
import com.vibesync.app.ui.components.SoundBarsIndicator
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary

private enum class QueueTab { UP_NEXT, HISTORY }

/** "Up Next" -- everything queued after the currently-playing track, drag-reorderable; "History"
 *  -- songs actually played, most recent first. Matches the reference mockup's two-tab layout. */
@Composable
fun QueueScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state by viewModel.playbackState.collectAsStateWithLifecycle()
    val upNext by viewModel.upNext.collectAsStateWithLifecycle()
    val history by viewModel.playbackHistory.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(QueueTab.UP_NEXT) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Queue",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        QueueTabSelector(selected = selectedTab, onSelect = { selectedTab = it })

        Text(
            text = "Now playing",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
        )
        QueueRow(
            title = state.title ?: "Nothing playing",
            thumbnailUrl = state.thumbnailUrl,
            isCurrent = true,
            trailing = { if (state.isPlaying) SoundBarsIndicator() }
        )

        when (selectedTab) {
            QueueTab.UP_NEXT -> {
                Text(
                    text = "Up next",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (upNext.isEmpty()) {
                    EmptyQueueMessage("Nothing queued up next.\nUse \"Add to queue\" on any song.")
                } else {
                    DragReorderColumn(
                        items = upNext,
                        key = { it.videoId },
                        onOrderChanged = { newOrder -> viewModel.reorderQueue(newOrder) },
                        modifier = Modifier.fillMaxSize()
                    ) { track, _, dragModifier ->
                        QueueRow(
                            title = track.title,
                            thumbnailUrl = track.thumbnailUrl,
                            isCurrent = false,
                            onClick = { viewModel.playFromUpNext(track) },
                            trailing = {
                                Icon(
                                    imageVector = Icons.Filled.DragHandle,
                                    contentDescription = "Drag to reorder",
                                    tint = TextSecondary,
                                    modifier = Modifier.padding(8.dp).then(dragModifier)
                                )
                            }
                        )
                    }
                }
            }
            QueueTab.HISTORY -> {
                Text(
                    text = "Recently played",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (history.isEmpty()) {
                    EmptyQueueMessage("Songs you play will show up here.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                        items(history, key = { it.videoId }) { track ->
                            QueueRow(
                                title = track.title,
                                thumbnailUrl = track.thumbnailUrl,
                                isCurrent = false,
                                onClick = { viewModel.playTrackDirectly(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueTabSelector(selected: QueueTab, onSelect: (QueueTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp)
    ) {
        QueueTab.values().forEach { tab ->
            val isSelected = tab == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tab == QueueTab.UP_NEXT) "Up Next" else "History",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) com.vibesync.app.ui.theme.SurfaceWhite else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun EmptyQueueMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QueueRow(
    title: String,
    thumbnailUrl: String?,
    isCurrent: Boolean,
    onClick: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().aspectRatio(1f).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = if (isCurrent) NavyPrimary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        )
        trailing?.invoke()
    }
}
