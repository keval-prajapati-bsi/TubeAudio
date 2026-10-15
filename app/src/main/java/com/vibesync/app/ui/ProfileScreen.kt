package com.vibesync.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary
import com.vibesync.app.ui.formatBytes

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ProfileScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val name by viewModel.profileName.collectAsStateWithLifecycle()
    val subtitle by viewModel.profileSubtitle.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
    val downloadTreeUri by viewModel.downloadTreeUri.collectAsStateWithLifecycle()
    val pendingInviteCount by viewModel.pendingInviteCount.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showDownloadLocationChoice by remember { mutableStateOf(false) }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.setDownloadLocation(uri.toString())
        }
    }

    LaunchedEffect(Unit) { viewModel.refreshStorageInfo() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openSettings() }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { showEditDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = SurfaceWhite
                    )
                }
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider()

            ProfileRow(
                icon = Icons.Filled.AccountCircle,
                label = "Account",
                onClick = { showEditDialog = true }
            )
            ProfileRow(
                icon = Icons.Filled.Palette,
                label = "Appearance",
                onClick = { viewModel.openSettings() }
            )
            ProfileRow(
                icon = Icons.Filled.People,
                label = "Friends",
                onClick = { viewModel.closeProfile(); viewModel.openFriends() }
            )
            ProfileRow(
                icon = Icons.Filled.NotificationsNone,
                label = "Party invites",
                trailingText = if (pendingInviteCount > 0) pendingInviteCount.toString() else null,
                onClick = { viewModel.closeProfile(); viewModel.openNotifications() }
            )
            ProfileRow(
                icon = Icons.Filled.Tune,
                label = "Music preferences",
                onClick = {
                    viewModel.closeProfile()
                    viewModel.openPreferencesEditor()
                }
            )
            ProfileRow(
                icon = Icons.Filled.Equalizer,
                label = "Equalizer",
                onClick = {
                    viewModel.closeProfile()
                    viewModel.openEqualizer()
                }
            )
            ProfileRow(
                icon = Icons.Filled.DownloadForOffline,
                label = "Downloads",
                onClick = {
                    viewModel.closeProfile()
                    viewModel.selectTab(AppTab.DOWNLOADS)
                }
            )
            ProfileRow(
                icon = Icons.Filled.Podcasts,
                label = "Podcasts",
                onClick = {
                    viewModel.closeProfile()
                    viewModel.selectTab(AppTab.PODCASTS)
                }
            )
            ProfileRow(
                icon = Icons.Filled.Wifi,
                label = "Local Room",
                onClick = {
                    viewModel.closeProfile()
                    viewModel.openLocalRoomHome()
                }
            )
            ProfileRow(
                icon = Icons.Filled.SdStorage,
                label = "Storage",
                trailingText = storageInfo?.let { "${formatBytes(it.usedByAppBytes)} used" },
                onClick = {
                    viewModel.closeProfile()
                    viewModel.selectTab(AppTab.DOWNLOADS)
                }
            )
            ProfileRow(
                icon = Icons.Filled.Folder,
                label = "Download location",
                trailingText = if (downloadTreeUri != null) "Custom folder" else "App storage",
                onClick = { showDownloadLocationChoice = true }
            )
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            initialName = name,
            initialSubtitle = subtitle,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName, newSubtitle -> viewModel.setProfile(newName, newSubtitle) }
        )
    }


    if (showDownloadLocationChoice) {
        AlertDialog(
            onDismissRequest = { showDownloadLocationChoice = false },
            title = { Text("Download location") },
            text = {
                Text(
                    "Choose where new downloads are saved. This only applies to songs you " +
                        "download after changing it -- existing downloads stay where they are.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDownloadLocationChoice = false
                    folderPicker.launch(null)
                }) { Text("Choose a folder") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDownloadLocationChoice = false
                    viewModel.setDownloadLocation(null)
                }) { Text("Use app storage") }
            }
        )
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    trailingText: String? = null,
    labelColor: androidx.compose.ui.graphics.Color? = null,
    iconColor: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor ?: TextSecondary)
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = labelColor ?: MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialSubtitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var subtitle by remember { mutableStateOf(initialSubtitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit profile") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, subtitle)
                onDismiss()
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
