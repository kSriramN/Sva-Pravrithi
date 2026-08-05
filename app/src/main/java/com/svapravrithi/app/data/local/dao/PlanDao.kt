package com.svapravrithi.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.svapravrithi.app.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: PlanEntity): Long

    @Update
    suspend fun update(plan: PlanEntity)

    @Delete
    suspend fun delete(plan: PlanEntity)

    @Query("SELECT * FROM plans WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun observeUpcoming(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun observeCompleted(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE yearMonth = :yearMonth")
    fun observeForMonth(yearMonth: String): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans")
    suspend fun getAllOnce(): List<PlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>)

    @Query("DELETE FROM plans")
    suspend fun clearAll()
}
