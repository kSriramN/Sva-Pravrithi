package com.svapravrithi.app.ui.screens.analytics

import com.svapravrithi.app.domain.model.formatAmount
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.theme.LocalCurrency
import com.svapravrithi.app.ui.components.BudgetProgressBar
import com.svapravrithi.app.ui.components.DonutChart
import com.svapravrithi.app.ui.components.DonutSlice
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Rajasik
import com.svapravrithi.app.ui.theme.Satvik
import com.svapravrithi.app.ui.theme.Tamasik
import com.svapravrithi.app.ui.theme.LocalMonthStartDay

@Composable
fun SpendingAnalyticsScreen(onBack: () -> Unit, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val monthStartDay = LocalMonthStartDay.current
    val currency = LocalCurrency.current
    val f = state.financials

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Analytics") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(DateUtil.cycleLabel(state.yearMonth, monthStartDay), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            val total = (f.actualNeeds + f.actualWants + f.actualPleasures).takeIf { it > 0 } ?: 1.0
            SvaCard {
                Text("Spending Distribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    DonutChart(
                        slices = listOf(
                            DonutSlice("Needs", f.actualNeeds, Satvik),
                            DonutSlice("Wants", f.actualWants, Rajasik),
                            DonutSlice("Pleasures", f.actualPleasures, Tamasik),
                        ),
                        size = 160.dp,
                        centerLabel = "${currency.symbol}${formatAmount(total, currency)}",
                        centerSubLabel = "Total Spent",
                    )
                }
            }

            SvaCard {
                Text("Needs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                BudgetProgressBar("Spent", f.actualNeeds, f.needsBudget, Satvik, planned = state.planned.needs)
            }
            SvaCard {
                Text("Wants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                BudgetProgressBar("Spent", f.actualWants, f.wantsBudget, Rajasik, planned = state.planned.wants)
            }
            SvaCard {
                Text("Pleasures", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                BudgetProgressBar("Spent", f.actualPleasures, f.pleasuresBudget, Tamasik, planned = state.planned.pleasures)
            }
        }
    }
}
