package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vibesync.app.lyrics.LyricsResult
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke

/** Best-effort lyrics for whatever's currently playing, via lrclib.net (see
 *  [com.vibesync.app.lyrics.LyricsRepository] for the matching caveat -- title-only search,
 *  no separate artist field to disambiguate with). */
@Composable
fun LyricsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state by viewModel.playbackState.collectAsStateWithLifecycle()
    val result by viewModel.lyricsResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingLyrics.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            LyricsThumbnail(state.thumbnailUrl)
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = state.title ?: "Lyrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Lyrics",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NavyPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Fetching lyrics...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            result is LyricsResult.Found -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BackgroundWhite)
                        .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "LYRICS",
                        style = MaterialTheme.typography.labelMedium,
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (result as LyricsResult.Found).text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 30.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            result is LyricsResult.Error -> LyricsMessage(
                title = "Couldn't load lyrics",
                message = (result as LyricsResult.Error).message
            )
            else -> LyricsMessage(
                title = "No lyrics found",
                message = "Lyrics are matched by title only, so this can miss for less common songs or unusual video titles."
            )
        }
    }
}

@Composable
private fun LyricsMessage(title: String, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                imageVector = Icons.Filled.MusicOff,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LyricsThumbnail(url: String?) {
    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
        }
    }
}
