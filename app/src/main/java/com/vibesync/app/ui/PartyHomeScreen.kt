package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.TextSecondary

/** Reached via the bottom nav's Menu -> Party Room entry. Two independent actions -- start your
 *  own party (get a shareable code) or join one someone else started (type their code) -- rather
 *  than one combined form, since they're genuinely different flows with different inputs. */
@Composable
fun PartyHomeScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var partyName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    val isLoading by viewModel.isPartyActionLoading.collectAsStateWithLifecycle()
    val error by viewModel.partyError.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

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
            Text(
                text = "Party Room",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Groups,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.padding(vertical = 12.dp).height(56.dp)
            )
        }
        Text(
            text = "Listen together in real time -- everyone in the room hears whatever the host plays, in sync.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )

        // -- Start a party --
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text("Start a party", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "You'll get a code to share with friends.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            OutlinedTextField(
                value = partyName,
                onValueChange = { partyName = it },
                placeholder = { Text("${user?.displayName ?: "My"}'s Party") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = partyFieldColors()
            )
            Button(
                onClick = { viewModel.createParty(partyName.trim()) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Create party")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(horizontal = 12.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(20.dp))

        // -- Join a party --
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text("Join a party", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "Enter the code someone shared with you.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            OutlinedTextField(
                value = joinCode,
                onValueChange = { joinCode = it.uppercase().take(6) },
                placeholder = { Text("e.g. K7P2QX") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth(),
                colors = partyFieldColors()
            )
            OutlinedButton(
                onClick = { viewModel.joinPartyByCode(joinCode) },
                enabled = !isLoading && joinCode.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Icon(Icons.Filled.Login, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Join party")
            }
        }

        if (isLoading) {
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
private fun partyFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedIndicatorColor = NavyPrimary
)
