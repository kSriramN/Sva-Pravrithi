package com.svapravrithi.app.ui.screens.analytics

import com.svapravrithi.app.domain.model.formatAmount
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.ui.theme.LocalCurrency
import com.svapravrithi.app.ui.components.DonutChart
import com.svapravrithi.app.ui.components.DonutSlice
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.LocalMonthStartDay

@Composable
fun GunaAnalyticsScreen(
    onBack: () -> Unit,
    onDeclareGoals: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val monthStartDay = LocalMonthStartDay.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guna Analysis") },
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

            if (!state.hasDeclaration) {
                SvaCard {
                    Text("No Monthly Declaration yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Declare your Savings Goal and Needs/Wants/Pleasures budgets to start seeing your Guna reflection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    PrimaryButton(text = "Declare This Month's Goals", onClick = onDeclareGoals)
                }
                return@Column
            }

            SvaCard {
                Text("Based on Monthly Reflection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    DonutChart(
                        slices = Guna.entries.map { g -> DonutSlice(g.label, state.gunaDistribution.percentages[g] ?: 0.0, g.color) },
                        size = 160.dp,
                        centerLabel = state.gunaDistribution.dominant.label,
                        centerSubLabel = "${state.gunaDistribution.percentOf(state.gunaDistribution.dominant)}%",
                    )
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                Guna.entries.forEach { g ->
                    LegendRow(g, state.gunaDistribution.percentOf(g))
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Text(state.gunaReason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            SvaCard {
                Text("How this was calculated", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(
                    "The percentages above come from how closely you're tracking your three declared goals this month \u2014 not from any individual purchase.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                ScoreLine("Savings score", state.reflection.savings.score, state.financials.savingsGoal, state.financials.actualSavings)
                ScoreLine("Wants score", state.reflection.wants.score, state.financials.wantsBudget, state.financials.actualWants)
                ScoreLine("Pleasures score", state.reflection.pleasures.score, state.financials.pleasuresBudget, state.financials.actualPleasures)
            }

            if (state.taggedGunaSpend.isNotEmpty()) {
                SvaCard {
                    Text("Your Personal Guna Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Optional tags you added on individual expenses \u2014 for your own reflection only, not used in your score.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    val total = state.taggedGunaSpend.values.sum().takeIf { it > 0 } ?: 1.0
                    Guna.entries.forEach { g ->
                        val amount = state.taggedGunaSpend[g] ?: 0.0
                        LegendRow(g, ((amount / total) * 100).toInt())
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreLine(label: String, score: Double, target: Double, actual: Double?) {
    val currency = LocalCurrency.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                if (actual == null) "not tracked yet" else "${currency.symbol}${formatAmount(actual, currency)} of ${currency.symbol}${formatAmount(target, currency)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text("${score.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LegendRow(guna: Guna, percent: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(guna.color))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        Text(guna.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text("$percent%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
