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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.svapravrithi.app.domain.engine.GunaDistribution
import com.svapravrithi.app.domain.model.Guna

/**
 * Concentric, blended tri-tone mandala representing the Satvik/Rajasik/Tamasik split.
 * Mirrors the "Guna Mandala" core visual in the design reference: three swirled rings
 * (green top, amber trailing, red base) with the dominant Guna's label centered.
 */
@Composable
fun GunaMandala(
    distribution: GunaDistribution,
    size: Dp = 180.dp,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = this.size.minDimension * 0.16f
            val radius = (this.size.minDimension - strokeWidth) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            val total = distribution.percentages.values.sum().takeIf { it > 0.0 } ?: 1.0
            var startAngle = -90f
            val order = listOf(Guna.SATVIK, Guna.RAJASIK, Guna.TAMASIK)
            order.forEach { guna ->
                val pct = (distribution.percentages[guna] ?: 0.0) / total
                val sweep = (pct * 360.0).toFloat().coerceAtLeast(0f)
                drawArc(
                    brush = Brush.sweepGradient(listOf(guna.color.copy(alpha = 0.55f), guna.color)),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Butt),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                )
                startAngle += sweep
            }
        }
        if (showLabel) {
            Box(contentAlignment = Alignment.Center) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = distribution.dominant.label.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = distribution.dominant.color,
                    )
                    Text(
                        text = "${distribution.percentOf(distribution.dominant)}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
