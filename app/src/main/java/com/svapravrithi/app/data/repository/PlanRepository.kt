package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.PlanDao
import com.svapravrithi.app.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(private val dao: PlanDao) {
    fun observeUpcoming(): Flow<List<PlanEntity>> = dao.observeUpcoming()
    fun observeCompleted(): Flow<List<PlanEntity>> = dao.observeCompleted()
    fun observeForMonth(yearMonth: String): Flow<List<PlanEntity>> = dao.observeForMonth(yearMonth)
    suspend fun save(plan: PlanEntity): Long = dao.upsert(plan)
    suspend fun update(plan: PlanEntity) = dao.update(plan)
    suspend fun delete(plan: PlanEntity) = dao.delete(plan)
    suspend fun markCompleted(plan: PlanEntity) = dao.update(plan.copy(isCompleted = true))
    suspend fun getAllOnce(): List<PlanEntity> = dao.getAllOnce()
    suspend fun replaceAll(plans: List<PlanEntity>) {
        dao.clearAll()
        dao.insertAll(plans)
    }
}
