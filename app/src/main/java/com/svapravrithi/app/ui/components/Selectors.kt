package com.svapravrithi.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PaymentMethod

@Composable
fun GunaSelector(
    selected: Guna?,
    onSelect: (Guna) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Guna.entries.forEach { guna ->
            FilterChip(
                selected = selected == guna,
                onClick = { onSelect(guna) },
                label = { androidx.compose.material3.Text(guna.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = guna.color,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun TypeSelector(
    selected: ExpenseType?,
    onSelect: (ExpenseType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ExpenseType.entries.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { androidx.compose.material3.Text(type.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selected: PaymentMethod?,
    onSelect: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PaymentMethod.entries.forEach { method ->
            FilterChip(
                selected = selected == method,
                onClick = { onSelect(method) },
                label = { androidx.compose.material3.Text(method.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
