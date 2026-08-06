package com.svapravrithi.app.domain.engine

import kotlin.math.roundToInt

data class CategoryScore(
    val score: Double,
    /** score - baseScore. Positive = bonus, negative = penalty, shown as "(+20 points)" in UI. */
    val delta: Double,
)

data class ReflectionResult(
    val savings: CategoryScore,
    val wants: CategoryScore,
    val pleasures: CategoryScore,
    val totalScore: Double,
    /** Nominal denominator for display, e.g. "135 / 150". Not a hard cap — see TDD, scores are unbounded. */
    val nominalMax: Double,
) {
    val roundedTotal: Int get() = totalScore.roundToInt()
}

/**
 * Implements the TDD "Reflection Engine & Monthly Guna Calculation" formulas verbatim:
 *
 * Savings:   goal met = baseScore; above goal = +bonus% exceeded; below goal = -penalty% shortfall
 * Wants:     within budget = baseScore; above = -(overspend% / divisor)
 * Pleasures: same logic as Wants, using its own divisor
 */
class ReflectionEngine(private val config: ScoringConfig = ScoringConfig()) {

    fun computeSavingsScore(goal: Double, actual: Double?): CategoryScore {
        if (goal <= 0.0 || actual == null) return CategoryScore(config.baseScore, 0.0)
        val delta = when {
            actual == goal -> 0.0
            actual > goal -> {
                val pctExceeded = ((actual - goal) / goal) * 100.0
                pctExceeded * config.savingsBonusMultiplier
            }
            else -> {
                val pctShortfall = ((goal - actual) / goal) * 100.0
                -(pctShortfall * config.savingsPenaltyMultiplier)
            }
        }
        return CategoryScore(config.baseScore + delta, delta)
    }

    fun computeWantsScore(budget: Double, actual: Double): CategoryScore =
        computeBudgetBoundScore(budget, actual, config.wantsDeductionDivisor)

    fun computePleasuresScore(budget: Double, actual: Double): CategoryScore =
        computeBudgetBoundScore(budget, actual, config.pleasureDeductionDivisor)

    private fun computeBudgetBoundScore(budget: Double, actual: Double, divisor: Double): CategoryScore {
        if (budget <= 0.0 || actual <= budget) return CategoryScore(config.baseScore, 0.0)
        val overspendPct = ((actual - budget) / budget) * 100.0
        val deduction = if (divisor != 0.0) overspendPct / divisor else 0.0
        return CategoryScore(config.baseScore - deduction, -deduction)
    }

    fun computeReflection(
        savingsGoal: Double,
        savingsActual: Double?,
        wantsBudget: Double,
        wantsActual: Double,
        pleasuresBudget: Double,
        pleasuresActual: Double,
    ): ReflectionResult {
        val savings = computeSavingsScore(savingsGoal, savingsActual)
        val wants = computeWantsScore(wantsBudget, wantsActual)
        val pleasures = computePleasuresScore(pleasuresBudget, pleasuresActual)
        return ReflectionResult(
            savings = savings,
            wants = wants,
            pleasures = pleasures,
            totalScore = savings.score + wants.score + pleasures.score,
            nominalMax = config.baseScore * 3,
        )
    }
}
