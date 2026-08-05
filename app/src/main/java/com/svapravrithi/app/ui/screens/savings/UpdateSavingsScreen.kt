package com.svapravrithi.app.ui.screens.savings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Satvik

@Composable
fun UpdateSavingsScreen(onBack: () -> Unit, viewModel: UpdateSavingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Savings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(DateUtil.monthLabel(state.yearMonth), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "Whenever you put money aside for savings or investment this month, update the total here. This is tracked separately from your expenses.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SvaCard {
                Text("Savings Goal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${"%,.0f".format(state.savingsGoal)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            }

            OutlinedTextField(
                value = state.actualSavingsInput,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Actual Savings Set Aside") },
                leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.saved) {
                Text("Saved!", style = MaterialTheme.typography.bodyMedium, color = Satvik)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            PrimaryButton(text = "Save", enabled = !state.isSaving, onClick = { viewModel.save() })
        }
    }
}
