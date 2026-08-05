package com.svapravrithi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DonutSlice(val label: String, val value: Double, val color: Color)

@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    centerLabel: String? = null,
    centerSubLabel: String? = null,
) {
    val total = slices.sumOf { it.value }.takeIf { it > 0.0 } ?: 1.0
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = this.size.minDimension * 0.22f
            val radius = (this.size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(this.size.width / 2f - radius, this.size.height / 2f - radius)
            var startAngle = -90f
            slices.forEach { slice ->
                val sweep = ((slice.value / total) * 360.0).toFloat()
                if (sweep > 0f) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep.coerceAtMost(359.5f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                        topLeft = topLeft,
                        size = Size(radius * 2, radius * 2),
                    )
                }
                startAngle += sweep
            }
        }
        if (centerLabel != null) {
            androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(centerLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (centerSubLabel != null) {
                    Text(centerSubLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
