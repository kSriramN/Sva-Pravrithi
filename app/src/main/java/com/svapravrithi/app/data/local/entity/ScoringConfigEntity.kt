package com.svapravrithi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row config table (id is always 0) backing the configurable rule engine from the TDD. */
@Entity(tableName = "scoring_config")
data class ScoringConfigEntity(
    @PrimaryKey val id: Int = 0,
    val baseScore: Double = 50.0,
    val wantsDeductionDivisor: Double = 2.0,
    val pleasureDeductionDivisor: Double = 2.0,
    val savingsBonusMultiplier: Double = 1.0,
    val savingsPenaltyMultiplier: Double = 1.0,
)
