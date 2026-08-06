package com.svapravrithi.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Satvik

@Composable
fun MonthStartDaySettingsScreen(onBack: () -> Unit, viewModel: MonthCycleViewModel = hiltViewModel()) {
    val currentStartDay by viewModel.startDay.collectAsState()
    val state by viewModel.uiState.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Month Start Day") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "If your salary or billing cycle doesn't start on the 1st, choose a different start day here. For example, choosing 25 means your \"month\" runs from the 25th to the 24th of the next month.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SvaCard {
                Text("Currently", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (currentStartDay == 1) "Starts on the 1st (calendar month)" else "Starts on day $currentStartDay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "This cycle: ${DateUtil.cycleLabel(DateUtil.currentCycleKey(currentStartDay), currentStartDay)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text("Choose a new start day (1\u201328)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().size(240.dp),
            ) {
                items((1..28).toList()) { day ->
                    FilterChip(
                        selected = state.startDay == day,
                        onClick = { viewModel.onStartDaySelected(day) },
                        label = { Text(day.toString()) },
                    )
                }
            }

            if (state.saved) {
                Text("Saved \u2014 existing entries updated to match.", style = MaterialTheme.typography.bodyMedium, color = Satvik)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            PrimaryButton(
                text = "Apply",
                enabled = !state.isSaving && state.startDay != currentStartDay,
                onClick = { showConfirm = true },
            )

            if (state.isSaving) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                    Text("Updating existing expenses and plans\u2026", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Change month start day?") },
            text = {
                Text(
                    "This will re-group every existing expense and plan you've logged to match the new cycle. " +
                        "Monthly Declarations you've already made will keep their original period. This can't be undone automatically.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    viewModel.apply()
                }) { Text("Apply") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            },
        )
    }
}
