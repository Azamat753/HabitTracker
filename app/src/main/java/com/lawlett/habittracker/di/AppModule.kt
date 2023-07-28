package com.lawlett.habittracker.di

import android.content.Context
import androidx.room.Room
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
}