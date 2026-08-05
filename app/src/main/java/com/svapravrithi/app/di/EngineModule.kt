package com.svapravrithi.app.di

import android.content.SharedPreferences
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    /** Lightweight prefs used for the active month + onboarding-seen flag. */
    @Provides
    @Singleton
    fun provideAppPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("sva_pravrithi_prefs", Context.MODE_PRIVATE)
}
