package com.svapravrithi.app.ui.screens.analytics

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.LocalMonthStartDay

@Composable
fun AnalyticsOverviewScreen(
    onOpenGuna: () -> Unit,
    onOpenSpending: () -> Unit,
    onOpenSavings: () -> Unit,
    onOpenReflection: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val monthStartDay = LocalMonthStartDay.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
        Text(DateUtil.cycleLabel(state.yearMonth, monthStartDay), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

        SvaCard {
            Text("Reflection Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            Text(
                "${state.reflection.roundedTotal} / ${state.reflection.nominalMax.toInt()}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.SemiBold,
                color = state.gunaDistribution.dominant.color,
            )
            Text("Predominantly ${state.gunaDistribution.dominant.label}", style = MaterialTheme.typography.bodyLarge)
        }

        AnalyticsLink(icon = Icons.Filled.SelfImprovement, title = "Guna Analytics", subtitle = "Sattvik / Rajasik / Tamasik distribution", onClick = onOpenGuna)
        AnalyticsLink(icon = Icons.Filled.PieChart, title = "Spending Analytics", subtitle = "Needs, Wants & Pleasures breakdown", onClick = onOpenSpending)
        AnalyticsLink(icon = Icons.Filled.Savings, title = "Savings Analytics", subtitle = "Goal progress & savings score", onClick = onOpenSavings)
        AnalyticsLink(icon = Icons.Filled.BarChart, title = "Monthly Reflection", subtitle = "Full breakdown & insight", onClick = onOpenReflection)
    }
}

@Composable
private fun AnalyticsLink(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    SvaCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
