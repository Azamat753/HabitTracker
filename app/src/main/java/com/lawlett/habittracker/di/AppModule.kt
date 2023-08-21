package com.lawlett.habittracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.lawlett.habittracker.Repository.Companion.KEY_PREF
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "habit-file")
            .build()

    @Provides
    fun provideHabitDao(@ApplicationContext context: Context) = provideDataBase(context).habitDao()

    @Provides
    fun provideFirebaseHelper() = FirebaseHelper()

    @Provides
    fun provideCacheManager(@ApplicationContext context: Context) = CacheManager(context)

    @Provides
    @Singleton
    fun providePreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)
    }
}