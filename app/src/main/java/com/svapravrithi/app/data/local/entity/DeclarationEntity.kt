package com.svapravrithi.app.data.local.entity

import androidx.room.Entity

/**
 * One declaration per calendar month, keyed by "yyyyMM".
 *
 * savingsGoal/needsBudget/wantsBudget/pleasuresBudget are declared upfront, at the
 * start of the month, in that order.
 *
 * actualSavings is NOT derived from income or from the other budgets — it's a habit
 * the user tracks directly: the amount they've actually put aside for investment.
 * The user updates this value themselves (see Profile > Update Savings) whenever
 * they set money aside; it defaults to 0 and is independent of expense tracking.
 */
@Entity(tableName = "declarations", primaryKeys = ["yearMonth"])
data class DeclarationEntity(
    val yearMonth: String,
    val savingsGoal: Double,
    val needsBudget: Double,
    val wantsBudget: Double,
    val pleasuresBudget: Double,
    val actualSavings: Double = 0.0,
)
