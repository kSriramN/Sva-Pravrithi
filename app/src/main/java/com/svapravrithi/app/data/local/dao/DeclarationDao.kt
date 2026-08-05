package com.svapravrithi.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svapravrithi.app.data.local.entity.DeclarationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeclarationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(declaration: DeclarationEntity)

    @Query("SELECT * FROM declarations WHERE yearMonth = :yearMonth")
    fun observe(yearMonth: String): Flow<DeclarationEntity?>

    @Query("SELECT * FROM declarations WHERE yearMonth = :yearMonth")
    suspend fun get(yearMonth: String): DeclarationEntity?

    @Query("SELECT * FROM declarations")
    suspend fun getAllOnce(): List<DeclarationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(declarations: List<DeclarationEntity>)

    @Query("DELETE FROM declarations")
    suspend fun clearAll()
}
