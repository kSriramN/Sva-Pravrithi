package com.svapravrithi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Satvik,
    onPrimary = Neutral100,
    primaryContainer = SatvikContainer,
    onPrimaryContainer = Satvik,
    secondary = Rajasik,
    onSecondary = Neutral100,
    secondaryContainer = RajasikContainer,
    onSecondaryContainer = Color(0xFF7A5200),
    tertiary = Tamasik,
    onTertiary = Neutral100,
    tertiaryContainer = TamasikContainer,
    onTertiaryContainer = Tamasik,
    error = Tamasik,
    background = Neutral200,
    onBackground = Neutral800,
    surface = Neutral100,
    onSurface = Neutral800,
    surfaceVariant = Neutral200,
    onSurfaceVariant = Neutral500,
    outline = Color(0xFFD0D5DD),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF66BB6A),
    onPrimary = Color(0xFF07230A),
    primaryContainer = Color(0xFF1B4620),
    onPrimaryContainer = Color(0xFFB6E6B9),
    secondary = Color(0xFFFFC947),
    onSecondary = Color(0xFF3D2900),
    secondaryContainer = Color(0xFF5A3E00),
    onSecondaryContainer = Color(0xFFFFE0A3),
    tertiary = Color(0xFFEF5350),
    onTertiary = Color(0xFF3B0A08),
    tertiaryContainer = Color(0xFF5C1613),
    onTertiaryContainer = Color(0xFFFFCDD2),
    error = Color(0xFFEF5350),
    background = DarkBackground,
    onBackground = Neutral100,
    surface = DarkSurface,
    onSurface = Neutral100,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = Color(0xFF3A4245),
)

@Composable
fun SvaPravrithiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = SvaTypography,
        shapes = SvaShapes,
        content = content,
    )
}
