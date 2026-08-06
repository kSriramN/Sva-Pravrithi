package com.svapravrithi.app.ui.screens.savings

import com.svapravrithi.app.domain.model.formatAmount
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
fun UpdateSavingsScreen(onBack: () -> Unit, onSaved: () -> Unit, viewModel: UpdateSavingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val currency = com.svapravrithi.app.ui.theme.LocalCurrency.current

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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
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
                Text("${currency.symbol}${formatAmount(state.savingsGoal, currency)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            }

            OutlinedTextField(
                value = state.actualSavingsInput,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Actual Savings Set Aside") },
                leadingIcon = { Text(currency.symbol, style = MaterialTheme.typography.titleMedium) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.svapravrithi.app.ui.components.CurrencyVisualTransformation(currency),
                modifier = Modifier.fillMaxWidth(),
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            PrimaryButton(text = "Save", enabled = !state.isSaving, onClick = { viewModel.save(onSaved) })
        }
    }
}
