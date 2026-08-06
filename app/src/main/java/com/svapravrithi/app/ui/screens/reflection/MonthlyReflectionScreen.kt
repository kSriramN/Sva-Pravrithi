package com.svapravrithi.app.ui.screens.reflection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.GunaMandala
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.LocalMonthStartDay
import com.svapravrithi.app.ui.screens.analytics.AnalyticsViewModel

@Composable
fun MonthlyReflectionScreen(onBack: () -> Unit, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val monthStartDay = LocalMonthStartDay.current
    val r = state.reflection

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Reflection") },
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

            SvaCard {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GunaMandala(distribution = state.gunaDistribution, size = 150.dp)
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Text(
                    "Predominantly ${state.gunaDistribution.dominant.label}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = state.gunaDistribution.dominant.color,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                Text(
                    state.gunaReason,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SvaCard {
                Text("Reflection Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "${r.roundedTotal} / ${r.nominalMax.toInt()}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                ScoreRow("Savings", r.savings.score, r.savings.delta)
                ScoreRow("Wants", r.wants.score, r.wants.delta)
                ScoreRow("Pleasures", r.pleasures.score, r.pleasures.delta)
            }

            SvaCard {
                Text("Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                Text(
                    buildString {
                        appendLine(savingsSummary(r.savings.delta))
                        appendLine(wantsSummary(r.wants.delta))
                        append(pleasuresSummary(r.pleasures.delta))
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun ScoreRow(label: String, score: Double, delta: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row {
            Text(score.toInt().toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
            Text(
                if (delta >= 0) "(+${delta.toInt()})" else "(${delta.toInt()})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun savingsSummary(delta: Double) = when {
    delta > 0 -> "You exceeded your savings goal."
    delta < 0 -> "You fell short of your savings goal."
    else -> "You met your savings goal exactly."
}

private fun wantsSummary(delta: Double) = when {
    delta < 0 -> "Wants spending went over your declared budget."
    else -> "Wants stayed within your declared budget."
}

private fun pleasuresSummary(delta: Double) = when {
    delta < 0 -> "Pleasure spending was above your planned budget."
    else -> "Pleasure spending stayed within your planned budget."
}
