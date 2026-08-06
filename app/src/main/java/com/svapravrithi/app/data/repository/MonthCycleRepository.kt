package com.svapravrithi.app.data.repository

import android.content.SharedPreferences
import com.svapravrithi.app.domain.model.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_START_DAY = "month_start_day"
const val DEFAULT_MONTH_START_DAY = 1

/**
 * Stores which day of the month a "month" is considered to start on (1-28, so every month
 * including February can support any value). Default is 1, matching a normal calendar month.
 *
 * Per product decision, changing this setting RECALCULATES every existing expense and plan's
 * stored month-grouping to match the new cycle immediately (see [setStartDay]), so historical
 * data and newly-declared cycles stay consistent with each other. Monthly Declarations are the
 * one exception: a declaration has no underlying raw timestamp of its own (it's already just a
 * label for "the goals for period X"), so there's nothing meaningful to recalculate - existing
 * declarations keep their original period label; only new declarations use the new cycle.
 */
@Singleton
class MonthCycleRepository @Inject constructor(
    private val prefs: SharedPreferences,
    private val expenseRepository: ExpenseRepository,
    private val planRepository: PlanRepository,
) {
    private val _startDay = MutableStateFlow(prefs.getInt(KEY_START_DAY, DEFAULT_MONTH_START_DAY))
    val startDay: StateFlow<Int> = _startDay.asStateFlow()

    suspend fun setStartDay(newStartDay: Int) {
        val clamped = newStartDay.coerceIn(1, 28)
        val old = _startDay.value
        if (clamped == old) return

        // Recalculate every expense's stored month-grouping against the new cycle.
        expenseRepository.getAllOnce().forEach { expense ->
            val newKey = DateUtil.cycleKeyFor(expense.date, clamped)
            if (newKey != expense.yearMonth) {
                expenseRepository.update(expense.copy(yearMonth = newKey))
            }
        }

        // Same for plans, keyed off their due date.
        planRepository.getAllOnce().forEach { plan ->
            val newKey = DateUtil.cycleKeyFor(plan.dueDate, clamped)
            if (newKey != plan.yearMonth) {
                planRepository.update(plan.copy(yearMonth = newKey))
            }
        }

        // Monthly Declarations are NOT recalculated - see class doc for why.

        prefs.edit().putInt(KEY_START_DAY, clamped).apply()
        _startDay.value = clamped
    }
}
