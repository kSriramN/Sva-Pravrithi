package com.svapravrithi.app.ui.screens.plan

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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.PlanPriority
import com.svapravrithi.app.ui.components.GunaSelector
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.TypeSelector

@Composable
fun AddPlanScreen(
    planId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddPlanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(planId) { viewModel.load(planId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (planId == null) "Add Plan" else "Edit Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.estimatedAmount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Estimated Amount") },
                leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "Due: ${state.dueDateLabel}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                TypeSelector(selected = state.type, onSelect = viewModel::onTypeChange)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Guna", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                GunaSelector(selected = state.guna, onSelect = viewModel::onGunaChange)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Priority", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlanPriority.entries.forEach { p ->
                        androidx.compose.material3.FilterChip(
                            selected = state.priority == p,
                            onClick = { viewModel.onPriorityChange(p) },
                            label = { Text(p.label) },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
            )

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            PrimaryButton(text = "Save Plan", enabled = !state.isSaving, onClick = { viewModel.save(onSaved) })
        }
    }
}
