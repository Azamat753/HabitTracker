package com.lawlett.habittracker.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lawlett.habittracker.models.GoodHabitModel
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface GoodHabitDao {

    @Insert
    suspend fun insert(model: GoodHabitModel)

    @Query("SELECT * FROM good_habit_table")
    fun getAll(): Flow<List<GoodHabitModel>>

    @Query("UPDATE good_habit_table SET allDays=:allDays WHERE id = :id")
    suspend fun updateAllDays(allDays: Int, id: Int)

    @Query("UPDATE good_habit_table SET currentDay=:currentDay,lastDate=:lastDate WHERE id = :id")
    suspend fun updateCurrentDay(currentDay: Int,lastDate:Date, id: Int)

    @Delete
    suspend fun delete(goodHabitModel: GoodHabitModel)
}