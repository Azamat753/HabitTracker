package com.lawlett.habittracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.lawlett.habittracker.Repository.Companion.KEY_PREF
import com.lawlett.habittracker.api.FirebaseApi
import com.lawlett.habittracker.api.SignApi
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.Key.KEY_PREF
import com.lawlett.habittracker.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideApi(client: OkHttpClient) =
        Retrofit.Builder().baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create()).client(client)
            .build().create(FirebaseApi::class.java)

    @Provides
    fun provideSApi(client: OkHttpClient) =
        Retrofit.Builder().baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create()).client(client)
            .build().create(SignApi::class.java)

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient().newBuilder()
            .connectTimeout(2000, TimeUnit.SECONDS)
            .writeTimeout(2000, TimeUnit.SECONDS)
            .readTimeout(2000, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

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
        return context.getSharedPreferences(KEY_PREF, MODE_PRIVATE)
    }
}