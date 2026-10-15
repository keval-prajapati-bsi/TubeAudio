package com.vibesync.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

private val Languages = listOf(
    "Hindi", "English", "Punjabi", "Tamil", "Telugu", "Bengali", "Marathi", "Gujarati", "Kannada",
    "Malayalam", "Urdu", "Odia", "Assamese", "Bhojpuri", "Rajasthani", "Haryanvi",
    "Spanish", "Korean", "Arabic", "French"
)
private val Genres = listOf(
    "Romance", "Party", "Workout", "Chill", "Hip-Hop", "Devotional", "Rock", "Classical", "Sad",
    "Dance", "Pop", "Indie", "Lo-fi", "EDM", "Folk", "Sufi", "Ghazal", "Bollywood", "Retro",
    "Instrumental", "Wedding", "Travel", "Motivational", "Kids", "Jazz"
)

/** Shown once on first launch (before the main app), and reusable from Profile ("Music
 *  preferences") to edit picks later -- [isEditMode] just swaps the button/back-arrow chrome,
 *  the picker itself is identical either way. Selections drive the Home screen's "Recommended"
 *  section (see [com.vibesync.app.data.RecommendationPreferences.buildRecommendedQuery]). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialLanguages: Set<String> = emptySet(),
    initialGenres: Set<String> = emptySet(),
    isEditMode: Boolean = false,
    onBack: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    onDone: (Set<String>, Set<String>) -> Unit
) {
    var selectedLanguages by remember { mutableStateOf(initialLanguages) }
    var selectedGenres by remember { mutableStateOf(initialGenres) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        if (isEditMode && onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = "What do you like to listen to?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = if (isEditMode) 8.dp else 24.dp)
        )
        Text(
            text = "Pick a few so we can recommend better songs for you on the Home tab.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Languages",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Languages.forEach { language ->
                PreferenceChip(
                    label = language,
                    selected = selectedLanguages.contains(language),
                    onClick = {
                        selectedLanguages = if (selectedLanguages.contains(language)) {
                            selectedLanguages - language
                        } else {
                            selectedLanguages + language
                        }
                    }
                )
            }
        }

        Text(
            text = "Moods & genres",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Genres.forEach { genre ->
                PreferenceChip(
                    label = genre,
                    selected = selectedGenres.contains(genre),
                    onClick = {
                        selectedGenres = if (selectedGenres.contains(genre)) {
                            selectedGenres - genre
                        } else {
                            selectedGenres + genre
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { onDone(selectedLanguages, selectedGenres) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (isEditMode) "Save" else "Continue", color = SurfaceWhite, style = MaterialTheme.typography.titleMedium)
        }

        if (!isEditMode && onSkip != null) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onSkip) {
                    Text("Skip for now", color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PreferenceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = SurfaceWhite
        )
    )
}
