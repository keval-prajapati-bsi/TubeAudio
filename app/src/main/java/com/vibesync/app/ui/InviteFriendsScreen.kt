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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.social.Friend
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Host-only, reached from the party room's kebab menu -- invite one of your friends straight
 *  into the party currently open, per item 7 of the request. Sending an invite doesn't move them
 *  anywhere itself; they see it via their own Notifications page (in-app -- see
 *  NotificationsScreen's doc comment) and choose whether to join. */
@Composable
fun InviteFriendsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val invited by viewModel.invitedFriendUids.collectAsStateWithLifecycle()
    val sorted = remember(friends) { friends.sortedByDescending { it.isOnline } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Invite friends", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (friends.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.People, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No friends yet", color = MaterialTheme.colorScheme.onSurface)
                    Text("Add friends from Profile -> Friends first.", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sorted, key = { it.uid }) { friend ->
                    InviteRow(
                        friend = friend,
                        alreadyInvited = friend.uid in invited,
                        onInvite = { viewModel.inviteFriendToParty(friend) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InviteRow(friend: Friend, alreadyInvited: Boolean, onInvite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(NavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(friend.displayName.take(1).uppercase().ifBlank { "?" }, style = MaterialTheme.typography.titleSmall, color = SurfaceWhite)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (friend.isOnline) Color(0xFF4CAF50) else TextSecondary)
            )
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(friend.displayName.ifBlank { "Someone" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (friend.isOnline) "Online" else "Offline", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        if (alreadyInvited) {
            Icon(Icons.Filled.Check, contentDescription = "Invited", tint = TextSecondary)
        } else {
            TextButton(onClick = onInvite) { Text("Invite") }
        }
    }
}
