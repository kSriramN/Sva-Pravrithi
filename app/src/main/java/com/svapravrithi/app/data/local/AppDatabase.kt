package com.svapravrithi.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.svapravrithi.app.data.local.dao.CategoryDao
import com.svapravrithi.app.data.local.dao.DeclarationDao
import com.svapravrithi.app.data.local.dao.ExpenseDao
import com.svapravrithi.app.data.local.dao.FaqDao
import com.svapravrithi.app.data.local.dao.PlanDao
import com.svapravrithi.app.data.local.dao.ScoringConfigDao
import com.svapravrithi.app.data.local.entity.CategoryEntity
import com.svapravrithi.app.data.local.entity.DeclarationEntity
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.local.entity.FaqEntity
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity

@Database(
    entities = [
        ExpenseEntity::class,
        PlanEntity::class,
        DeclarationEntity::class,
        ScoringConfigEntity::class,
        CategoryEntity::class,
        FaqEntity::class,
    ],
    // Bumped for the new Categories/FAQ tables. fallbackToDestructiveMigration() in
    // DatabaseModule means this wipes local data on upgrade rather than crashing -
    // acceptable pre-release; add a real Migration before shipping to real users.
    // v2 -> v3: added ExpenseEntity.paymentMethod (Cash/Card/Account, optional).
    version = 3,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun planDao(): PlanDao
    abstract fun declarationDao(): DeclarationDao
    abstract fun scoringConfigDao(): ScoringConfigDao
    abstract fun categoryDao(): CategoryDao
    abstract fun faqDao(): FaqDao
}
