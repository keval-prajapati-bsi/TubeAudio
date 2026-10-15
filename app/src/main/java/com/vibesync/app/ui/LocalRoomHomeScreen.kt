package com.vibesync.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.data.localparty.DiscoveredRoom
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

/** Reached via the bottom nav's Menu -> Local Room entry. The LAN counterpart to Party Room --
 *  no Firebase, no internet, no account/code needed beyond being on the same WiFi -- host a room
 *  (advertised via local network discovery) or browse rooms other phones nearby are already
 *  hosting. See `localparty/LocalPartyHost.kt`'s doc comment for how this actually works
 *  end-to-end. */
@Composable
fun LocalRoomHomeScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var roomName by remember { mutableStateOf("") }
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val rooms by viewModel.localDiscoveredRooms.collectAsStateWithLifecycle()
    val isDiscovering by viewModel.isDiscoveringLocalRooms.collectAsStateWithLifecycle()
    val isConnecting by viewModel.isConnectingLocalRoom.collectAsStateWithLifecycle()
    val error by viewModel.localRoomError.collectAsStateWithLifecycle()

    // NSD (local network discovery) is gated by different runtime permissions across Android
    // versions -- requested here, once, and discovery only actually starts once that request has
    // been resolved (granted or not) so the very first visit to this screen doesn't race a
    // permission prompt against discovery already having tried and failed. See
    // AndroidManifest.xml's comment on these two permissions for why both are declared.
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        viewModel.startLocalDiscovery()
    }
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(permissions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Local Room", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Wifi,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.padding(vertical = 12.dp).height(56.dp)
            )
        }
        Text(
            text = "Listen together over the same WiFi -- no internet or account sign-in needed beyond " +
                "being on this network. Nothing here leaves your local network.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )

        // -- Host a room --
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text("Host a room", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "Everyone nearby on this WiFi will be able to find and join it.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            OutlinedTextField(
                value = roomName,
                onValueChange = { roomName = it },
                placeholder = { Text("${user?.displayName ?: "My"}'s Room") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = localFieldColors()
            )
            Button(
                onClick = { viewModel.hostLocalRoom(roomName.trim()) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Start hosting")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(horizontal = 12.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(20.dp))

        // -- Join a room --
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Rooms nearby",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (isDiscovering) CircularProgressIndicator(modifier = Modifier.height(18.dp), color = NavyPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (rooms.isEmpty()) {
                Text(
                    text = if (isDiscovering) "Looking for rooms on this WiFi..." else "No rooms found nearby yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            } else {
                rooms.forEach { room ->
                    DiscoveredRoomRow(
                        room = room,
                        enabled = !isConnecting,
                        onClick = { viewModel.joinLocalRoom(room) }
                    )
                }
            }
        }

        if (isConnecting) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        }

        if (error != null) {
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DiscoveredRoomRow(room: DiscoveredRoom, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(NavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Router, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.padding(10.dp))
        }
        Text(
            text = room.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        )
        Text("Join", style = MaterialTheme.typography.labelLarge, color = NavyPrimary)
    }
}

@Composable
private fun localFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedIndicatorColor = NavyPrimary
)
