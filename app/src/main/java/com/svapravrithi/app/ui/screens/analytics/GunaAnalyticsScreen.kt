package com.svapravrithi.app.ui.screens.analytics

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
import androidx.compose.ui.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.ui.components.DonutChart
import com.svapravrithi.app.ui.components.DonutSlice
import com.svapravrithi.app.ui.components.SvaCard

@Composable
fun GunaAnalyticsScreen(onBack: () -> Unit, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

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
            Text(DateUtil.monthLabel(state.yearMonth), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
private fun LegendRow(guna: Guna, percent: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(guna.color))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        Text(guna.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text("$percent%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
