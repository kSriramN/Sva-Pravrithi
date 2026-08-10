package com.svapravrithi.app.ui.screens.home

import com.svapravrithi.app.domain.model.formatAmount
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.theme.LocalCurrency
import com.svapravrithi.app.ui.theme.LocalMonthStartDay
import com.svapravrithi.app.ui.components.BudgetProgressBar
import com.svapravrithi.app.ui.components.GunaMandala
import com.svapravrithi.app.ui.components.SvaCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onOpenDeclaration: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPlan: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val currency = LocalCurrency.current
    val monthStartDay = LocalMonthStartDay.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onOpenDeclaration) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
            Text(
                "Sva-Pravrithi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onOpenProfile) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        SvaCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Current Spending Reflection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        DateUtil.cycleLabel(state.yearMonth, monthStartDay),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GunaMandala(distribution = state.gunaDistribution, size = 170.dp)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
            Text(
                "${currency.symbol}${formatAmount(state.totalSpent, currency)} spent this month",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        val pagerState = rememberPagerState(pageCount = { 3 })
        val pagerScope = rememberCoroutineScope()
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                listOf("Needs", "Wants", "Pleasures").forEachIndexed { index, label ->
                    val selected = pagerState.currentPage == index
                    Text(
                        label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { pagerScope.launch { pagerState.animateScrollToPage(index) } },
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            HorizontalPager(state = pagerState) { page ->
                val breakdown = when (page) {
                    0 -> state.needs
                    1 -> state.wants
                    else -> state.pleasures
                }
                SvaCard {
                    Text("Total Spent", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${currency.symbol}${formatAmount(breakdown.total, currency)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (breakdown.budget > 0) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                        BudgetProgressBar(
                            label = "of budget",
                            spent = breakdown.total,
                            budget = breakdown.budget,
                            accent = MaterialTheme.colorScheme.primary,
                            planned = breakdown.planned,
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                    Text("Recent ${breakdown.type.label}s", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                    if (breakdown.recent.isEmpty()) {
                        Text("No entries yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        breakdown.recent.forEach { expense ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditExpense(expense.id) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(expense.category, style = MaterialTheme.typography.bodyLarge)
                                    Text(DateUtil.dayLabel(expense.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("${currency.symbol}${formatAmount(expense.amount, currency)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        if (state.categoryBreakdown.isNotEmpty()) {
            SvaCard {
                Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    com.svapravrithi.app.ui.components.DonutChart(
                        slices = state.categoryBreakdown.map {
                            com.svapravrithi.app.ui.components.DonutSlice(it.category, it.amount, it.color)
                        },
                        size = 160.dp,
                        centerLabel = "${currency.symbol}${formatAmount(state.totalSpent, currency)}",
                        centerSubLabel = "Total Spent",
                    )
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                state.categoryBreakdown.forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(slice.color),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("${slice.percent}%", style = MaterialTheme.typography.labelMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                        Text(slice.category, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Text("${currency.symbol}${formatAmount(slice.amount, currency)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
    }
}
