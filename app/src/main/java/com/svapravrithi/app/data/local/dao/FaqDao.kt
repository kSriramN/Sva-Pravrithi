package com.svapravrithi.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svapravrithi.app.data.local.entity.FaqEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaqDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faq: FaqEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(faqs: List<FaqEntity>)

    @Delete
    suspend fun delete(faq: FaqEntity)

    @Query("SELECT * FROM faqs ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<FaqEntity>>

    @Query("SELECT COUNT(*) FROM faqs")
    suspend fun count(): Int
}
