package com.svapravrithi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Horizontal "spent of budget" bar, colored by the given accent (e.g. Guna or category color).
 * If [planned] > 0, a muted marker segment shows committed-but-not-yet-spent future amounts
 * (from upcoming Plan items) as an indicator only \u2014 it's not part of the scored total. */
@Composable
fun BudgetProgressBar(
    label: String,
    spent: Double,
    budget: Double,
    accent: Color,
    modifier: Modifier = Modifier,
    planned: Double = 0.0,
) {
    val ratio = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
    val plannedRatio = if (budget > 0) ((spent + planned) / budget).toFloat().coerceIn(0f, 1f) else 0f
    val overBudget = spent > budget && budget > 0
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(
                "₹${"%,.0f".format(spent)} / ₹${"%,.0f".format(budget)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(accent.copy(alpha = 0.15f)),
        ) {
            if (plannedRatio > ratio) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(plannedRatio)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = 0.35f)),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (overBudget) MaterialTheme.colorScheme.error else accent),
            )
        }
        if (planned > 0) {
            Text(
                "+ ₹${"%,.0f".format(planned)} planned (upcoming, not yet spent)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
