package com.vibesync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.ui.formatDuration
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Plain checkbox-and-play-nothing row used only in the export "pick songs" selection mode. */
@Composable
fun SelectableTrackRow(
    track: TrackEntity,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isSelected, onCheckedChange = null)

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            if (track.thumbnailUrl != null) {
                AsyncImage(
                    model = track.thumbnailUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDuration(track.durationSeconds * 1000),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
