package com.svapravrithi.app.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.BudgetProgressBar
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Satvik

@Composable
fun SavingsAnalyticsScreen(onBack: () -> Unit, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val f = state.financials
    val achievementPct = if (f.savingsGoal > 0) (f.actualSavings / f.savingsGoal * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Savings Analytics") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(DateUtil.monthLabel(state.yearMonth), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            SvaCard {
                Text("Savings Goal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${"%,.0f".format(f.savingsGoal)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                Text("Actual Saved", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("₹${"%,.0f".format(f.actualSavings)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = Satvik)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                    Text("$achievementPct%", style = MaterialTheme.typography.titleMedium, color = Satvik)
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                BudgetProgressBar("Progress", f.actualSavings.coerceAtLeast(0.0), f.savingsGoal.coerceAtLeast(1.0), Satvik)
            }

            SvaCard {
                Text("Savings Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "${state.reflection.savings.score.toInt()}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Satvik,
                )
                val delta = state.reflection.savings.delta.toInt()
                Text(
                    if (delta >= 0) "(+$delta points)" else "($delta points)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
