package com.vibesync.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** The reference treats search and discovery as one destination. V32 keeps both real data flows
 * while making them a compact Explore space rather than competing bottom-navigation tabs. */
@Composable
fun ExploreScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var selected by rememberSaveable { mutableStateOf(0) }
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
            ExplorePill("Discover", selected == 0, Modifier.weight(1f)) { selected = 0 }
            ExplorePill("Trending", selected == 1, Modifier.weight(1f).padding(start = 8.dp)) { selected = 1 }
        }
        if (selected == 0) SearchScreen(viewModel = viewModel) else TrendingScreen(viewModel = viewModel)
    }
}

@Composable
private fun ExplorePill(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(label, modifier = Modifier.padding(vertical = 10.dp), style = MaterialTheme.typography.labelLarge)
    }
}
