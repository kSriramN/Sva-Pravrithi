package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.ExpenseDao
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(private val dao: ExpenseDao) {
    fun observeForMonth(yearMonth: String): Flow<List<ExpenseEntity>> = dao.observeForMonth(yearMonth)
    fun observeRecentForMonth(yearMonth: String, limit: Int = 5): Flow<List<ExpenseEntity>> =
        dao.observeRecentForMonth(yearMonth, limit)
    fun observeAll(): Flow<List<ExpenseEntity>> = dao.observeAll()
    suspend fun save(expense: ExpenseEntity): Long = dao.upsert(expense)
    suspend fun update(expense: ExpenseEntity) = dao.update(expense)
    suspend fun getById(id: Long): ExpenseEntity? = dao.getById(id)
    suspend fun delete(expense: ExpenseEntity) = dao.delete(expense)
    suspend fun getAllOnce(): List<ExpenseEntity> = dao.getAllOnce()
    suspend fun replaceAll(expenses: List<ExpenseEntity>) {
        dao.clearAll()
        dao.insertAll(expenses)
    }
}
