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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vibesync.app.data.party.PartyMember
import com.vibesync.app.data.party.PartyQueueItem
import com.vibesync.app.data.party.QueueItemStatus
import com.vibesync.app.extractor.SearchResult
import com.vibesync.app.ui.components.ConfirmDialog
import com.vibesync.app.ui.theme.BackgroundWhite
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** The live room: who's in it, what's playing (host-controlled, everyone else's device follows
 *  via [com.vibesync.app.playback.PartySyncController]), and the request/approve queue. Closing
 *  this screen (back button) doesn't leave the party -- sync keeps running in the background,
 *  reachable again via the bottom nav's Menu; only the explicit "Leave party" action actually
 *  leaves. */
@Composable
fun PartyRoomScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val isJoining by viewModel.isJoiningParty.collectAsStateWithLifecycle()
    if (isJoining) {
        JoiningPartyScreen()
        return
    }

    val party by viewModel.partyState.collectAsStateWithLifecycle()
    val queue by viewModel.partyQueue.collectAsStateWithLifecycle()
    val members by viewModel.partyMembers.collectAsStateWithLifecycle()
    val isHost by viewModel.isPartyHost.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val error by viewModel.partyError.collectAsStateWithLifecycle()
    val code by viewModel.activePartyCode.collectAsStateWithLifecycle()
    val myRequestCount by viewModel.myPartyRequestCount.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showAddSongDialog by remember { mutableStateOf(false) }
    if (showAddSongDialog) {
        BackHandler { showAddSongDialog = false }
        AddSongToPartyScreen(viewModel = viewModel, onBack = { showAddSongDialog = false })
        return
    }

    var showLeaveConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var kickTarget by remember { mutableStateOf<PartyMember?>(null) }

    val pending = remember(queue) { queue.filter { it.status == QueueItemStatus.PENDING }.sortedBy { it.addedAtMillis } }
    // Excludes whatever's currently playing -- that item stays status=APPROVED in Firestore the
    // whole time it's current (see PartyRepository's doc comment), so "Up next" has to filter it
    // out by id, not by status alone.
    val approved = remember(queue, party?.currentQueueItemId) {
        queue.filter { it.status == QueueItemStatus.APPROVED && it.id != party?.currentQueueItemId }.sortedBy { it.position }
    }

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
                    text = party?.name ?: "Party",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Code: ${code ?: ""}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            IconButton(onClick = {
                code?.let {
                    clipboardManager.setText(AnnotatedString(it))
                    Toast.makeText(context, "Party code copied", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy code", tint = TextSecondary)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = TextSecondary)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    if (isHost) {
                        DropdownMenuItem(
                            text = { Text("Invite friends") },
                            leadingIcon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                            onClick = { showMenu = false; viewModel.openInviteFriends() }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Leave party", color = ErrorRed) },
                        onClick = { showMenu = false; showLeaveConfirm = true }
                    )
                }
            }
        }

        if (members.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(members, key = { it.uid }) { member ->
                    val isMemberHost = party?.hostUid == member.uid
                    MemberChip(
                        member = member,
                        isHost = isMemberHost,
                        // Only the host can remove people, can't remove themself, and can't remove
                        // whoever else the host field points to (there's only ever one host).
                        onClick = if (isHost && !isMemberHost) { { kickTarget = member } } else null
                    )
                }
            }
        }

        NowPlayingPartyCard(
            title = party?.currentTitle,
            thumbnailUrl = party?.currentThumbnailUrl,
            hostName = party?.hostName,
            isPlaying = playbackState.isPlaying,
            positionMs = playbackState.positionMs,
            durationMs = playbackState.durationMs,
            isHost = isHost,
            onPlayPause = viewModel::togglePartyPlayPause,
            onPrevious = viewModel::skipPartyToPrevious,
            onNext = viewModel::skipPartyToNext,
            onSeek = viewModel::seekPartyTo
        )

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)) {
            if (isHost && pending.isNotEmpty()) {
                item { SectionLabel("Requests") }
                items(pending, key = { it.id }) { item ->
                    PendingRequestRow(
                        item = item,
                        onApprove = { viewModel.approvePartyQueueItem(item) },
                        onReject = { viewModel.rejectPartyQueueItem(item) }
                    )
                }
            }

            item { SectionLabel("Up next") }
            if (approved.isEmpty()) {
                item {
                    Text(
                        text = "Nothing queued yet -- tap \"Add a song\" below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            } else {
                items(approved, key = { it.id }) { item ->
                    val index = approved.indexOf(item)
                    ApprovedQueueRow(
                        item = item,
                        isHost = isHost,
                        canMoveUp = index > 0,
                        canMoveDown = index < approved.lastIndex,
                        onPlayNow = { viewModel.playApprovedQueueItemNow(item) },
                        onMoveUp = {
                            val newOrder = approved.toMutableList()
                            newOrder.removeAt(index)
                            newOrder.add(index - 1, item)
                            viewModel.reorderPartyQueue(newOrder)
                        },
                        onMoveDown = {
                            val newOrder = approved.toMutableList()
                            newOrder.removeAt(index)
                            newOrder.add(index + 1, item)
                            viewModel.reorderPartyQueue(newOrder)
                        },
                        onMoveToTop = {
                            val newOrder = approved.toMutableList()
                            newOrder.removeAt(index)
                            newOrder.add(0, item)
                            viewModel.reorderPartyQueue(newOrder)
                        },
                        onMoveToBottom = {
                            val newOrder = approved.toMutableList()
                            newOrder.removeAt(index)
                            newOrder.add(item)
                            viewModel.reorderPartyQueue(newOrder)
                        },
                        onRemove = { viewModel.removePartyQueueItem(item) }
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.BottomEnd) {
        Column(horizontalAlignment = Alignment.End) {
            if (myRequestCount > 0) {
                // The ask this answers: a non-host member had no way to tell how many of their
                // own requests were still sitting in the queue (pending or approved, not yet
                // played) -- this is that count, visible right where they'd add another one.
                Text(
                    text = if (myRequestCount == 1) "You've requested 1 song" else "You've requested $myRequestCount songs",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Button(onClick = { showAddSongDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Add a song")
            }
        }
    }

    if (showLeaveConfirm) {
        ConfirmDialog(
            title = "Leave party?",
            message = "You'll stop hearing what's playing here until you rejoin with the code.",
            confirmLabel = "Leave",
            onConfirm = { viewModel.leaveCurrentParty(); onBack() },
            onDismiss = { showLeaveConfirm = false }
        )
    }

    kickTarget?.let { member ->
        ConfirmDialog(
            title = "Remove ${member.displayName.ifBlank { "this member" }}?",
            message = "They'll be removed from the party and stop hearing what's playing.",
            confirmLabel = "Remove",
            onConfirm = { viewModel.kickPartyMember(member.uid) },
            onDismiss = { kickTarget = null }
        )
    }

    error?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissPartyError,
            confirmButton = { TextButton(onClick = viewModel::dismissPartyError) { Text("OK") } },
            text = { Text(message) }
        )
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
private fun MemberChip(member: PartyMember, isHost: Boolean, onClick: (() -> Unit)?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isHost) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.displayName.take(1).uppercase().ifBlank { "?" },
                style = MaterialTheme.typography.titleMedium,
                color = if (isHost) SurfaceWhite else MaterialTheme.colorScheme.onSurface
            )
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(ErrorRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PersonRemove, contentDescription = "Remove ${member.displayName}", tint = SurfaceWhite, modifier = Modifier.size(10.dp))
                }
            }
        }
        Text(
            text = if (isHost) "${member.displayName.ifBlank { "Host" }} (Host)" else member.displayName.ifBlank { "Guest" },
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp).size(width = 60.dp, height = 16.dp)
        )
    }
}

@Composable
private fun NowPlayingPartyCard(
    title: String?,
    thumbnailUrl: String?,
    hostName: String?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    isHost: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
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
                    text = if (hostName != null) "Hosted by $hostName" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            if (isHost) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = NavyPrimary)
                }
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
                // Host-only: an actual draggable timeline -- affects everyone in the party, not
                // just this device (see MainViewModel.seekPartyTo). Tracks a local drag position
                // while dragging so the thumb doesn't fight the real (sync-driven) position on
                // every recomposition mid-drag, only committing the seek on release.
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
private fun PendingRequestRow(item: PartyQueueItem, onApprove: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QueueThumbnail(item.thumbnailUrl)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Requested by ${item.addedByName.ifBlank { "someone" }}", style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1)
        }
        IconButton(onClick = onApprove) { Icon(Icons.Filled.Check, contentDescription = "Approve", tint = NavyPrimary) }
        IconButton(onClick = onReject) { Icon(Icons.Filled.Close, contentDescription = "Reject", tint = ErrorRed) }
    }
}

/** Host-only: the whole row is tappable ([onPlayNow], "pick this song directly") in addition to
 *  the up/down reorder buttons -- tapping the thumbnail/title jumps straight to that song, tapping
 *  an arrow just reorders without changing what's currently playing. The single-step up/down
 *  arrows alone made reordering a long way (e.g. last song to first) a tap-per-position slog --
 *  the overflow menu's "Move to top"/"Move to bottom" cover that in one tap; "Remove from queue"
 *  is the host-only real delete this row didn't have at all before (previously the only way to
 *  drop a song was to play through it). */
@Composable
private fun ApprovedQueueRow(
    item: PartyQueueItem,
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
        QueueThumbnail(item.thumbnailUrl)
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

@Composable
private fun QueueThumbnail(url: String?) {
    Box(
        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(BackgroundWhite),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().aspectRatio(1f).clip(RoundedCornerShape(10.dp))
            )
        } else {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = TextSecondary)
        }
    }
}

/** Shown while this device's audio is still catching up to the party's live position -- see
 *  `PartySyncController.isSyncing`. Deliberately a dead-end (no back button): there's nothing
 *  useful to interact with yet, and it resolves itself in well under a second normally. */
@Composable
private fun JoiningPartyScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = NavyPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Joining party...", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                "Syncing up with what's already playing",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** Search YouTube (separate state from the main Search tab, see [MainViewModel.partyAddQuery])
 *  and request either an individual song or -- via the "Queue a playlist" section -- one of your
 *  own saved playlists in full. A real full screen rather than a dialog -- the previous
 *  `AlertDialog` version cramped the text field and results list into the dialog's constrained
 *  content area badly enough that the field was effectively untappable/unusable in practice; a
 *  full screen has proper room to scroll and doesn't fight the dialog's own sizing. */
@Composable
private fun AddSongToPartyScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val query by viewModel.partyAddQuery.collectAsStateWithLifecycle()
    val results by viewModel.partyAddResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isPartyAddSearching.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val queuedVideoIds by viewModel.partyQueuedVideoIds.collectAsStateWithLifecycle()
    var showPlaylistPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = if (showPlaylistPicker) "Queue a playlist" else "Add a song",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showPlaylistPicker) {
            TextButton(onClick = { showPlaylistPicker = false }) { Text("Search a song instead") }
            if (playlists.isEmpty()) {
                Text(
                    "You don't have any playlists yet.",
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(playlists, key = { it.id }) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.requestPlaylistForPartyById(playlist.id)
                                    onBack()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.PlaylistPlay, contentDescription = null, tint = NavyPrimary)
                            Text(playlist.name, modifier = Modifier.padding(start = 12.dp), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        } else {
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
            TextButton(onClick = { showPlaylistPicker = true }, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Filled.PlaylistPlay, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Queue a whole playlist instead")
            }

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
                        AddSongResultRow(
                            result = result,
                            alreadyQueued = alreadyQueued,
                            onClick = {
                                if (!alreadyQueued) {
                                    viewModel.requestSearchResultForParty(result)
                                    Toast.makeText(context, "Added \"${result.title}\"", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Matches the same "+ becomes a checkmark once added" pattern already used for adding a song to
 *  a playlist elsewhere in the app -- a toast confirms the tap immediately, and the icon itself
 *  reactively flips once `partyQueuedVideoIds` catches up (near-instant off the locally-cached
 *  queue state, not waiting on a Firestore round trip). */
@Composable
private fun AddSongResultRow(result: SearchResult, alreadyQueued: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QueueThumbnail(result.thumbnailUrl)
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
