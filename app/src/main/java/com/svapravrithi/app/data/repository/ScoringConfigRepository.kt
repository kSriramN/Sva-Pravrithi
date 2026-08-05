package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.ScoringConfigDao
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity
import com.svapravrithi.app.domain.engine.ScoringConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoringConfigRepository @Inject constructor(private val dao: ScoringConfigDao) {

    fun observe(): Flow<ScoringConfig> = dao.observe().map { it?.toDomain() ?: ScoringConfig() }

    suspend fun get(): ScoringConfig = dao.get()?.toDomain() ?: ScoringConfig()

    suspend fun save(config: ScoringConfig) = dao.upsert(
        ScoringConfigEntity(
            baseScore = config.baseScore,
            wantsDeductionDivisor = config.wantsDeductionDivisor,
            pleasureDeductionDivisor = config.pleasureDeductionDivisor,
            savingsBonusMultiplier = config.savingsBonusMultiplier,
            savingsPenaltyMultiplier = config.savingsPenaltyMultiplier,
        )
    )

    suspend fun ensureSeeded() {
        if (dao.get() == null) save(ScoringConfig())
    }

    private fun ScoringConfigEntity.toDomain() = ScoringConfig(
        baseScore = baseScore,
        wantsDeductionDivisor = wantsDeductionDivisor,
        pleasureDeductionDivisor = pleasureDeductionDivisor,
        savingsBonusMultiplier = savingsBonusMultiplier,
        savingsPenaltyMultiplier = savingsPenaltyMultiplier,
    )
}
