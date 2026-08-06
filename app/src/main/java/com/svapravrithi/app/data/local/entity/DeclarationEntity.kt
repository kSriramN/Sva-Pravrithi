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
 * they set money aside. It's null until the user explicitly records a value for
 * the month — this is deliberate: null means "not tracked yet" (no penalty applied
 * by the Reflection Engine), which is different from an explicit 0 (a real shortfall).
 */
@Entity(tableName = "declarations", primaryKeys = ["yearMonth"])
data class DeclarationEntity(
    val yearMonth: String,
    val savingsGoal: Double,
    val needsBudget: Double,
    val wantsBudget: Double,
    val pleasuresBudget: Double,
    val actualSavings: Double? = null,
)
