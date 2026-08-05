package com.svapravrithi.app.domain.engine

/**
 * Externalized scoring rules per TDD section "Configurable Rule Engine".
 * Persisted in Room (see ScoringConfigEntity) so values can be tuned without a code change.
 * Defaults below match the TDD's worked examples exactly. No score is capped in v1.0,
 * per product decision — these are the only tunables.
 */
data class ScoringConfig(
    val baseScore: Double = 50.0,
    val wantsDeductionDivisor: Double = 2.0,
    val pleasureDeductionDivisor: Double = 2.0,
    val savingsBonusMultiplier: Double = 1.0,
    val savingsPenaltyMultiplier: Double = 1.0,
)
