package com.svapravrithi.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Satvik

@Composable
fun ScoringSettingsScreen(onBack: () -> Unit, viewModel: ScoringSettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scoring Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = { TextButton(onClick = { viewModel.resetToDefaults() }) { Text("Reset") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "These control how your Reflection Score is calculated \u2014 see the app's README (\"Where the business logic lives\") for the exact formulas. Only change these if you understand the effect; the defaults match the original design.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SvaCard {
                Text("Base Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("Starting points for each pillar (Savings/Wants/Pleasures) before any bonus or deduction.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.baseScore,
                    onValueChange = viewModel::onBaseScoreChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Wants Overspend Divisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("Points deducted = overspend% \u00f7 this number. Higher = more forgiving.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.wantsDeductionDivisor,
                    onValueChange = viewModel::onWantsDivisorChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Pleasures Overspend Divisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("Same idea, for the Pleasures budget.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.pleasureDeductionDivisor,
                    onValueChange = viewModel::onPleasureDivisorChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Savings Bonus Multiplier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("How strongly exceeding your Savings Goal is rewarded.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.savingsBonusMultiplier,
                    onValueChange = viewModel::onSavingsBonusChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Savings Penalty Multiplier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("How strongly falling short of your Savings Goal is penalized.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.savingsPenaltyMultiplier,
                    onValueChange = viewModel::onSavingsPenaltyChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.saved) {
                Text("Saved!", style = MaterialTheme.typography.bodyMedium, color = Satvik)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            PrimaryButton(text = "Save", enabled = !state.isSaving, onClick = { viewModel.save() })
        }
    }
}
