package com.svapravrithi.app.ui.screens.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.PlanPriority

@Composable
fun PlanListScreen(
    onAddPlan: () -> Unit,
    onEditPlan: (Long) -> Unit,
    viewModel: PlanListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlan, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, contentDescription = "Add Plan", tint = androidx.compose.ui.graphics.Color.White)
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "My Plans",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(20.dp),
            )
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Upcoming") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Completed") })
            }
            val items = if (tabIndex == 0) state.upcoming else state.completed
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (tabIndex == 0) "No upcoming plans yet" else "No completed plans yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items, key = { it.id }) { plan ->
                        PlanRow(plan = plan, onClick = { onEditPlan(plan.id) }, onComplete = { viewModel.markCompleted(plan) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanRow(plan: PlanEntity, onClick: () -> Unit, onComplete: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(plan.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                    PriorityBadge(plan.priority)
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "₹${"%,.0f".format(plan.estimatedAmount)} · ${plan.type.label} · ${plan.guna.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Due ${DateUtil.dayLabel(plan.dueDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!plan.isCompleted) {
                androidx.compose.material3.TextButton(onClick = onComplete) { Text("Done") }
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: PlanPriority) {
    val color = when (priority) {
        PlanPriority.HIGH -> MaterialTheme.colorScheme.error
        PlanPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
        PlanPriority.LOW -> MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(priority.label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
