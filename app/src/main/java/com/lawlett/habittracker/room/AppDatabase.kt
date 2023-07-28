package com.lawlett.habittracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lawlett.habittracker.models.HabitModel

@Database(entities = [HabitModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

}