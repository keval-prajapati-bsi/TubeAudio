package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.social.PartyInvite
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import androidx.compose.foundation.BorderStroke

/** Pending party invites from friends -- reached from Profile's badge-counted "Party invites" row.
 *  This is the "notification page" from item 7 of the request; it's deliberately in-app-only
 *  (no system notification while the app is fully closed), see
 *  [com.vibesync.app.data.social.SocialRepository.observeMyPendingInvites]'s doc comment for the
 *  disclosed reason (no backend, no Firebase Cloud Messaging trigger). */
@Composable
fun NotificationsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val invites by viewModel.pendingInvites.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Notifications", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (invites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No pending invites", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(invites, key = { it.id }) { invite ->
                    InviteRow(
                        invite = invite,
                        onAccept = { viewModel.respondToInvite(invite, accept = true) },
                        onReject = { viewModel.respondToInvite(invite, accept = false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InviteRow(invite: PartyInvite, onAccept: () -> Unit, onReject: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(NavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Groups, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = "${invite.fromDisplayName.ifBlank { "Someone" }} invited you to a party",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(invite.partyName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = onReject, colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)) {
                Text("Decline")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onAccept) { Text("Join") }
        }
    }
}
