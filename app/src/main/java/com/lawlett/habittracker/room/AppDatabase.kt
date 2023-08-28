package com.lawlett.habittracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.lawlett.habittracker.models.HabitModel

@Database(entities = [HabitModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

}