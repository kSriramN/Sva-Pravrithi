package com.svapravrithi.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.svapravrithi.app.data.local.dao.DeclarationDao
import com.svapravrithi.app.data.local.dao.ExpenseDao
import com.svapravrithi.app.data.local.dao.PlanDao
import com.svapravrithi.app.data.local.dao.ScoringConfigDao
import com.svapravrithi.app.data.local.entity.DeclarationEntity
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity

@Database(
    entities = [
        ExpenseEntity::class,
        PlanEntity::class,
        DeclarationEntity::class,
        ScoringConfigEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun planDao(): PlanDao
    abstract fun declarationDao(): DeclarationDao
    abstract fun scoringConfigDao(): ScoringConfigDao
}
