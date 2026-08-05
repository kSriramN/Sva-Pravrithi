package com.svapravrithi.app.di

import android.content.Context
import androidx.room.Room
import com.svapravrithi.app.data.local.AppDatabase
import com.svapravrithi.app.data.local.dao.DeclarationDao
import com.svapravrithi.app.data.local.dao.ExpenseDao
import com.svapravrithi.app.data.local.dao.PlanDao
import com.svapravrithi.app.data.local.dao.ScoringConfigDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "sva-pravrithi.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun providePlanDao(db: AppDatabase): PlanDao = db.planDao()

    @Provides
    fun provideDeclarationDao(db: AppDatabase): DeclarationDao = db.declarationDao()

    @Provides
    fun provideScoringConfigDao(db: AppDatabase): ScoringConfigDao = db.scoringConfigDao()
}
