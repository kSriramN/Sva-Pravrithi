package com.svapravrithi.app.ui.screens.declaration

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
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard

@Composable
fun MonthlyDeclarationScreen(
    yearMonth: String,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: MonthlyDeclarationViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(yearMonth) { viewModel.load(yearMonth) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Declaration") },
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
            Text(
                DateUtil.monthLabel(state.yearMonth),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Declare your monthly goals",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )

            SvaCard {
                Text("Savings Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.savingsGoal,
                    onValueChange = viewModel::onSavingsGoalChange,
                    label = { Text("₹ Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Needs Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.needsBudget,
                    onValueChange = viewModel::onNeedsBudgetChange,
                    label = { Text("₹ Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Wants Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.wantsBudget,
                    onValueChange = viewModel::onWantsBudgetChange,
                    label = { Text("₹ Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Pleasures Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = state.pleasuresBudget,
                    onValueChange = viewModel::onPleasuresBudgetChange,
                    label = { Text("₹ Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            PrimaryButton(
                text = "Save & Continue",
                enabled = !state.isSaving,
                onClick = { viewModel.save(onSaved) },
            )
        }
    }
}
