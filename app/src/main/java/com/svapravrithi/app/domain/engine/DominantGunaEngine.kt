package com.svapravrithi.app.domain.engine

import com.svapravrithi.app.domain.model.Guna

data class GunaReflectionResult(
    val dominant: Guna,
    val reason: String,
    /** Relative visual weights for the three Gunas, normalized to sum to ~100. Display only. */
    val visualWeights: Map<Guna, Double>,
)

/**
 * Derives the month's Dominant Guna from the three Reflection Engine pillar scores —
 * per product decision, Guna reflects adherence to *declared* goals, not individual
 * transactions. Per-expense Guna tags are optional and shown separately (Guna Analytics)
 * for personal reflection only; they do not feed this calculation.
 *
 * Rule (documented so it's easy to retune):
 *   Sattva -> Savings met/exceeded AND Wants overspend% <= tolerance AND Pleasures overspend% <= tolerance.
 *     A small tolerance band (default 2.5% overspend) still counts as "controlled" -
 *     a trivial ₹10 overspend on a ₹5,000 budget shouldn't disqualify an otherwise
 *     disciplined month.
 *   Otherwise, compare severities:
 *     tamasSeverity = savingsShortfall + pleasuresOverspend   (score-point terms)
 *     rajasSeverity = wantsOverspend                          (score-point terms)
 *   Whichever is larger wins; Tamas wins exact ties, and also wins whenever *both*
 *   goals were missed at once (confirmed: "targets not met AND wants/pleasures
 *   overspent" should read as Tamasik, not Rajasik).
 */
class DominantGunaEngine(private val toleranceOverspendPercent: Double = 2.5) {

    fun compute(reflection: ReflectionResult, config: ScoringConfig = ScoringConfig()): GunaReflectionResult {
        val baseScore = config.baseScore
        val dS = reflection.savings.score - baseScore
        val dW = reflection.wants.score - baseScore
        val dP = reflection.pleasures.score - baseScore

        val savingsSurplus = dS.coerceAtLeast(0.0)
        val savingsShortfall = (-dS).coerceAtLeast(0.0)
        val wantsOverspend = (-dW).coerceAtLeast(0.0)
        val pleasuresOverspend = (-dP).coerceAtLeast(0.0)

        // Convert the tolerance from "% overspend" into score-deduction terms using
        // each pillar's own deduction divisor, since that's the space wantsOverspend/
        // pleasuresOverspend are already expressed in (deduction = overspend% / divisor).
        val wantsToleranceDeduction = toleranceOverspendPercent / config.wantsDeductionDivisor
        val pleasuresToleranceDeduction = toleranceOverspendPercent / config.pleasureDeductionDivisor

        val isOnTrack = dS >= 0.0 &&
            wantsOverspend <= wantsToleranceDeduction &&
            pleasuresOverspend <= pleasuresToleranceDeduction

        val tamasSeverity = savingsShortfall + pleasuresOverspend
        val rajasSeverity = wantsOverspend

        val dominant: Guna
        val reason: String
        when {
            isOnTrack -> {
                dominant = Guna.SATVIK
                reason = "Savings goal met or exceeded, and Wants & Pleasures stayed within budget."
            }
            tamasSeverity > rajasSeverity -> {
                dominant = Guna.TAMASIK
                reason = "Savings fell short and Pleasures/Wants spending ran over budget."
            }
            rajasSeverity > tamasSeverity -> {
                dominant = Guna.RAJASIK
                reason = "Wants spending ran over budget while Savings & Pleasures stayed reasonably controlled."
            }
            else -> {
                dominant = Guna.TAMASIK
                reason = "Both Wants and your Savings/Pleasures adherence were off-track this month."
            }
        }

        val sattvaWeight = (savingsSurplus + 1.0) +
            (baseScore - wantsOverspend).coerceAtLeast(0.0) * 0.3 +
            (baseScore - pleasuresOverspend).coerceAtLeast(0.0) * 0.3
        val weights = mapOf(
            Guna.SATVIK to sattvaWeight,
            Guna.RAJASIK to (rajasSeverity + 1.0),
            Guna.TAMASIK to (tamasSeverity + 1.0),
        )
        val total = weights.values.sum()
        val normalized = weights.mapValues { (_, v) -> v / total * 100.0 }

        return GunaReflectionResult(dominant = dominant, reason = reason, visualWeights = normalized)
    }
}
