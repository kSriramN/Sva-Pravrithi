package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.DeclarationDao
import com.svapravrithi.app.data.local.entity.DeclarationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeclarationRepository @Inject constructor(private val dao: DeclarationDao) {
    fun observe(yearMonth: String): Flow<DeclarationEntity?> = dao.observe(yearMonth)
    suspend fun get(yearMonth: String): DeclarationEntity? = dao.get(yearMonth)
    suspend fun save(declaration: DeclarationEntity) = dao.upsert(declaration)
    suspend fun getAllOnce(): List<DeclarationEntity> = dao.getAllOnce()
    suspend fun replaceAll(declarations: List<DeclarationEntity>) {
        dao.clearAll()
        dao.insertAll(declarations)
    }
}
