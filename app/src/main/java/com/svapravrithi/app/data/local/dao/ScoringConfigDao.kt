package com.svapravrithi.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoringConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: ScoringConfigEntity)

    @Query("SELECT * FROM scoring_config WHERE id = 0")
    fun observe(): Flow<ScoringConfigEntity?>

    @Query("SELECT * FROM scoring_config WHERE id = 0")
    suspend fun get(): ScoringConfigEntity?
}
