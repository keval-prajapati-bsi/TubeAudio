package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.social.Friend
import com.vibesync.app.data.social.UserProfile
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Reached via Profile -> Friends. Search-by-username is what item 6 of the request actually
 *  asked for -- adding a friend is immediate and mutual (no request/approval step, unlike party
 *  invites below, which do need one since accepting means joining someone's live party). Online/
 *  offline is a heartbeat approximation, not real presence -- see
 *  [com.vibesync.app.data.social.ONLINE_WINDOW_MILLIS]'s doc comment. */
@Composable
fun FriendsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val myProfile by viewModel.myProfile.collectAsStateWithLifecycle()
    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val isSavingUsername by viewModel.isSavingUsername.collectAsStateWithLifecycle()
    val query by viewModel.friendSearchQuery.collectAsStateWithLifecycle()
    val results by viewModel.friendSearchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingFriends.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val friendUids = remember(friends) { friends.map { it.uid }.toSet() }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Friends", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(8.dp))

        UsernameCard(
            username = myProfile?.username,
            error = usernameError,
            isSaving = isSavingUsername,
            onSave = { viewModel.setUsername(it) },
            onDismissError = viewModel::dismissUsernameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onFriendSearchQueryChanged,
            placeholder = { Text("Search by username") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.submitFriendSearch() }),
            modifier = Modifier.fillMaxWidth()
        )

        if (isSearching) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        } else if (query.isNotBlank() && results.isEmpty()) {
            Text(
                "No one found with that username.",
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        } else if (results.isNotEmpty()) {
            LazyColumn(modifier = Modifier.height(220.dp)) {
                items(results, key = { it.uid }) { profile ->
                    SearchedUserRow(
                        profile = profile,
                        alreadyFriend = profile.uid in friendUids,
                        onAdd = { viewModel.addFriend(profile) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your friends",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (friends.isEmpty()) {
            Text(
                "No friends yet -- search for a username above to add one.",
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(friends, key = { it.uid }) { friend ->
                    FriendRow(friend = friend, onRemove = { viewModel.removeFriend(friend) })
                }
            }
        }
    }
}

@Composable
private fun UsernameCard(
    username: String?,
    error: String?,
    isSaving: Boolean,
    onSave: (String) -> Unit,
    onDismissError: () -> Unit
) {
    var editing by remember(username) { mutableStateOf(username == null) }
    var input by remember(username) { mutableStateOf(username ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        if (editing) {
            Text(
                if (username == null) "Choose a username so friends can find you" else "Edit your username",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it; if (error != null) onDismissError() },
                    placeholder = { Text("username") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onSave(input) }, enabled = input.isNotBlank() && !isSaving) {
                    if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = SurfaceWhite) else Text("Save")
                }
            }
            if (error != null) {
                Text(error, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
            }
            if (username != null) {
                TextButton(onClick = { editing = false }) { Text("Cancel") }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("@$username", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                TextButton(onClick = { editing = true }) { Text("Change") }
            }
        }
    }
}

@Composable
private fun SearchedUserRow(profile: UserProfile, alreadyFriend: Boolean, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(profile.displayName)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(profile.displayName.ifBlank { "Someone" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("@${profile.username}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        if (alreadyFriend) {
            Icon(Icons.Filled.Check, contentDescription = "Already friends", tint = TextSecondary)
        } else {
            IconButton(onClick = onAdd) {
                Icon(Icons.Filled.PersonAdd, contentDescription = "Add friend", tint = NavyPrimary)
            }
        }
    }
}

@Composable
private fun FriendRow(friend: Friend, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Avatar(friend.displayName)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (friend.isOnline) androidx.compose.ui.graphics.Color(0xFF4CAF50) else TextSecondary)
            )
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(friend.displayName.ifBlank { "Someone" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = (friend.username?.let { "@$it -- " } ?: "") + if (friend.isOnline) "Online" else "Offline",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.PersonRemove, contentDescription = "Remove friend", tint = TextSecondary)
        }
    }
}

@Composable
private fun Avatar(name: String) {
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(NavyPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(name.take(1).uppercase().ifBlank { "?" }, style = MaterialTheme.typography.titleSmall, color = SurfaceWhite)
    }
}
