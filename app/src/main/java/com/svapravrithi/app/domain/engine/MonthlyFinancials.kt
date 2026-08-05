package com.svapravrithi.app.domain.engine

/**
 * Plain holder for the month's declared budgets vs actual spend/savings, shared by
 * Home, Analytics, and Monthly Reflection so every screen reads the same shape.
 *
 * Note: actualSavings is NOT derived here — it's a value the user tracks and updates
 * directly (see Profile > Update Savings), independent of Needs/Wants/Pleasures spend.
 */
data class MonthlyFinancials(
    val savingsGoal: Double,
    val needsBudget: Double,
    val wantsBudget: Double,
    val pleasuresBudget: Double,
    val actualSavings: Double,
    val actualNeeds: Double,
    val actualWants: Double,
    val actualPleasures: Double,
)
