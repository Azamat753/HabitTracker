package com.lawlett.habittracker.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.lawlett.habittracker.models.GoodHabitModel
import com.lawlett.habittracker.models.BadHabitModel

@Database(
    entities = [
        BadHabitModel::class,
        GoodHabitModel::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AppDatabase.MyAutoMigration::class),
        AutoMigration(from = 2, to = 3, spec = AppDatabase.MyAutoMigration::class),
    ],
    version = 3,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    class MyAutoMigration : AutoMigrationSpec

    abstract fun badHabitDao(): BadHabitDao
    abstract fun goodHabitDao(): GoodHabitDao

}