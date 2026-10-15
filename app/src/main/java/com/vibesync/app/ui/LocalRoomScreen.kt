package com.vibesync.app.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vibesync.app.data.localparty.LocalMember
import com.vibesync.app.data.localparty.LocalQueueItem
import com.vibesync.app.extractor.SearchResult
import com.vibesync.app.ui.components.ConfirmDialog
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** The active local room: mirrors PartyRoomScreen's structure closely, adapted for
 *  [com.vibesync.app.data.localparty.LocalRoomState]'s flatter model (no pending/approve queue --
 *  see that class's doc comment). Closing this screen (back) doesn't leave the room, same "stays
 *  running in the background, reachable via Menu" pattern as the Firebase Party Room. */
@Composable
fun LocalRoomScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val isConnecting by viewModel.isConnectingLocalRoom.collectAsStateWithLifecycle()
    if (isConnecting) {
        ConnectingLocalRoomScreen()
        return
    }

    val room by viewModel.localRoomState.collectAsStateWithLifecycle()
    val isHost by viewModel.isLocalHost.collectAsStateWithLifecycle()
    val myId by viewModel.myLocalId.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val error by viewModel.localRoomError.collectAsStateWithLifecycle()

    var showAddSongScreen by remember { mutableStateOf(false) }
    if (showAddSongScreen) {
        BackHandler { showAddSongScreen = false }
        AddSongToLocalRoomScreen(viewModel = viewModel, room = room, onBack = { showAddSongScreen = false })
        return
    }

    var showLeaveConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var kickTarget by remember { mutableStateOf<LocalMember?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room?.roomName ?: "Local Room",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Local network -- no internet needed", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = TextSecondary)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(if (isHost) "Close room" else "Leave room", color = ErrorRed) },
                        onClick = { showMenu = false; showLeaveConfirm = true }
                    )
                }
            }
        }

        val members = room?.members ?: emptyList()
        if (members.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    LocalMemberChip(
                        member = member,
                        isMe = member.id == myId,
                        onClick = if (isHost && !member.isHost) { { kickTarget = member } } else null
                    )
                }
            }
        }

        NowPlayingLocalCard(
            title = room?.currentTitle,
            thumbnailUrl = room?.currentThumbnailUrl,
            isPlaying = playbackState.isPlaying,
            positionMs = playbackState.positionMs,
            durationMs = playbackState.durationMs,
            isHost = isHost,
            onPlayPause = viewModel::localTogglePlayPause,
            onNext = viewModel::localSkipNext,
            onSeek = viewModel::localSeekTo
        )

        val queue = room?.queue ?: emptyList()
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)) {
            item { SectionLabel("Up next") }
            if (queue.isEmpty()) {
                item {
                    Text(
                        text = "Nothing queued yet -- tap \"Add a song\" below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            } else {
                items(queue, key = { it.id }) { item ->
                    val index = queue.indexOf(item)
                    LocalQueueRow(
                        item = item,
                        isHost = isHost,
                        canMoveUp = index > 0,
                        canMoveDown = index < queue.lastIndex,
                        onPlayNow = { viewModel.localPlayQueueItemNow(item) },
                        onMoveUp = {
                            val newOrder = queue.toMutableList(); newOrder.removeAt(index); newOrder.add(index - 1, item)
                            viewModel.localReorderQueue(newOrder)
                        },
                        onMoveDown = {
                            val newOrder = queue.toMutableList(); newOrder.removeAt(index); newOrder.add(index + 1, item)
                            viewModel.localReorderQueue(newOrder)
                        },
                        onMoveToTop = {
                            val newOrder = queue.toMutableList(); newOrder.removeAt(index); newOrder.add(0, item)
                            viewModel.localReorderQueue(newOrder)
                        },
                        onMoveToBottom = {
                            val newOrder = queue.toMutableList(); newOrder.removeAt(index); newOrder.add(item)
                            viewModel.localReorderQueue(newOrder)
                        },
                        onRemove = { viewModel.localRemoveQueueItem(item) }
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.BottomEnd) {
        androidx.compose.material3.Button(onClick = { showAddSongScreen = true }) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
            Text("Add a song")
        }
    }

    if (showLeaveConfirm) {
        ConfirmDialog(
            title = if (isHost) "Close this room?" else "Leave room?",
            message = if (isHost) "Everyone connected will be disconnected." else "You'll stop hearing what's playing here.",
            confirmLabel = if (isHost) "Close" else "Leave",
            onConfirm = { viewModel.leaveLocalRoom(); onBack() },
            onDismiss = { showLeaveConfirm = false }
        )
    }

    kickTarget?.let { member ->
        ConfirmDialog(
            title = "Remove ${member.displayName.ifBlank { "this member" }}?",
            message = "They'll be disconnected from the room.",
            confirmLabel = "Remove",
            onConfirm = { viewModel.localKickMember(member.id); kickTarget = null },
            onDismiss = { kickTarget = null }
        )
    }

    error?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissLocalRoomError,
            confirmButton = { TextButton(onClick = viewModel::dismissLocalRoomError) { Text("OK") } },
            text = { Text(message) }
        )
    }
}

@Composable
private fun ConnectingLocalRoomScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = NavyPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Connecting...", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp)
    )
}

@Composable
private fun LocalMemberChip(member: LocalMember, isMe: Boolean, onClick: (() -> Unit)?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(if (member.isHost) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.displayName.take(1).uppercase().ifBlank { "?" },
                style = MaterialTheme.typography.titleMedium,
                color = if (member.isHost) SurfaceWhite else MaterialTheme.colorScheme.onSurface
            )
            if (onClick != null) {
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).size(16.dp).clip(CircleShape).background(ErrorRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PersonRemove, contentDescription = "Remove ${member.displayName}", tint = SurfaceWhite, modifier = Modifier.size(10.dp))
                }
            }
        }
        val label = when {
            member.isHost -> "${member.displayName.ifBlank { "Host" }} (Host)"
            isMe -> "${member.displayName.ifBlank { "Guest" }} (You)"
            else -> member.displayName.ifBlank { "Guest" }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp).size(width = 60.dp, height = 16.dp)
        )
    }
}

@Composable
private fun NowPlayingLocalCard(
    title: String?,
    thumbnailUrl: String?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    isHost: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(BackgroundWhite),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = title ?: "Nothing playing yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Local room",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            if (isHost) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = NavyPrimary
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = NavyPrimary)
                }
            }
        }
        if (durationMs > 0) {
            if (isHost) {
                var dragPositionMs by remember { mutableFloatStateOf(-1f) }
                Slider(
                    value = if (dragPositionMs >= 0f) dragPositionMs else positionMs.toFloat(),
                    onValueChange = { dragPositionMs = it },
                    onValueChangeFinished = {
                        if (dragPositionMs >= 0f) onSeek(dragPositionMs.toLong())
                        dragPositionMs = -1f
                    },
                    valueRange = 0f..durationMs.toFloat(),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    colors = SliderDefaults.colors(thumbColor = NavyPrimary, activeTrackColor = NavyPrimary, inactiveTrackColor = BackgroundWhite)
                )
            } else {
                LinearProgressIndicator(
                    progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(3.dp).clip(RoundedCornerShape(2.dp)),
                    color = NavyPrimary,
                    trackColor = BackgroundWhite
                )
            }
        }
    }
}

@Composable
private fun LocalQueueRow(
    item: LocalQueueItem,
    isHost: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onPlayNow: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onMoveToTop: () -> Unit,
    onMoveToBottom: () -> Unit,
    onRemove: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isHost) Modifier.clickable(onClick = onPlayNow) else Modifier)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            if (item.thumbnailUrl != null) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().aspectRatio(1f).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Added by ${item.addedByName.ifBlank { "someone" }}", style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1)
        }
        if (isHost) {
            Column {
                IconButton(onClick = onMoveUp, enabled = canMoveUp, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up", tint = if (canMoveUp) TextSecondary else CardBorder)
                }
                IconButton(onClick = onMoveDown, enabled = canMoveDown, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down", tint = if (canMoveDown) TextSecondary else CardBorder)
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = TextSecondary)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Move to top") },
                        leadingIcon = { Icon(Icons.Filled.KeyboardDoubleArrowUp, contentDescription = null) },
                        enabled = canMoveUp,
                        onClick = { showMenu = false; onMoveToTop() }
                    )
                    DropdownMenuItem(
                        text = { Text("Move to bottom") },
                        leadingIcon = { Icon(Icons.Filled.KeyboardDoubleArrowDown, contentDescription = null) },
                        enabled = canMoveDown,
                        onClick = { showMenu = false; onMoveToBottom() }
                    )
                    DropdownMenuItem(
                        text = { Text("Remove from queue", color = ErrorRed) },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = ErrorRed) },
                        onClick = { showMenu = false; onRemove() }
                    )
                }
            }
        }
    }
}

/** Search YouTube and request a song for the local room -- same "full screen, not a dialog"
 *  pattern as the Firebase party's add-song screen (see PartyRoomScreen.kt's AddSongToPartyScreen
 *  doc comment for why), reusing that same search state/debounce plumbing
 *  (`MainViewModel.partyAddQuery` etc.) since it's generic YouTube search unrelated to which room
 *  type is active -- the two features are mutually exclusive at any given moment anyway. */
@Composable
private fun AddSongToLocalRoomScreen(viewModel: MainViewModel, room: com.vibesync.app.data.localparty.LocalRoomState?, onBack: () -> Unit) {
    val query by viewModel.partyAddQuery.collectAsStateWithLifecycle()
    val results by viewModel.partyAddResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isPartyAddSearching.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val queuedVideoIds = remember(room) {
        (room?.queue?.map { it.videoId }.orEmpty() + listOfNotNull(room?.currentVideoId)).toSet()
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Add a song", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onPartyAddQueryChanged,
            placeholder = { Text("Search a song") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { viewModel.submitPartyAddSearch() }),
            trailingIcon = {
                IconButton(onClick = viewModel::submitPartyAddSearch) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = NavyPrimary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = NavyPrimary
            )
        )

        if (isSearching) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        } else if (query.isNotBlank() && results.isEmpty()) {
            Text(
                "No songs found for \"$query\".",
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results, key = { it.videoId }) { result ->
                    val alreadyQueued = result.videoId in queuedVideoIds
                    LocalAddSongResultRow(
                        result = result,
                        alreadyQueued = alreadyQueued,
                        onClick = {
                            if (!alreadyQueued) {
                                viewModel.requestSearchResultForLocalRoom(result)
                                Toast.makeText(context, "Added \"${result.title}\"", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalAddSongResultRow(result: SearchResult, alreadyQueued: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            if (result.thumbnailUrl != null) {
                AsyncImage(
                    model = result.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().aspectRatio(1f).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
            }
        }
        Text(
            text = result.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
        )
        if (alreadyQueued) {
            Icon(Icons.Filled.Check, contentDescription = "Already added", tint = TextSecondary)
        } else {
            Icon(Icons.Filled.Add, contentDescription = "Request", tint = NavyPrimary)
        }
    }
}
