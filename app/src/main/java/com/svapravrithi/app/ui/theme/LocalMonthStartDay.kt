package com.svapravrithi.app.ui.theme

import androidx.compose.runtime.compositionLocalOf
import com.svapravrithi.app.data.repository.DEFAULT_MONTH_START_DAY

/**
 * The app-wide month-start-day setting (Profile > Month Start Day), provided once at the
 * root (see MainActivity) and read anywhere a month label or a "current cycle" needs it
 * inside a Composable via `LocalMonthStartDay.current`. ViewModels that need it (to compute
 * a cycle key at save time, or the initial "current cycle" at construction) inject
 * MonthCycleRepository directly instead, since CompositionLocal only works in @Composable code.
 */
val LocalMonthStartDay = compositionLocalOf { DEFAULT_MONTH_START_DAY }
