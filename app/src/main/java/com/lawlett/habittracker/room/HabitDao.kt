package com.lawlett.habittracker.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lawlett.habittracker.models.HabitModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(model: HabitModel)

    @Query("SELECT * FROM habit_table")
    fun getAll(): Flow<List<HabitModel>>

    @Update
    suspend fun update(model: HabitModel)

    @Delete
    suspend fun delete(model: HabitModel)

    @Query("SELECT history FROM habit_table WHERE id = :id ")
    fun getHistory(id: Int): Flow<String>

    @Query("UPDATE habit_table SET history=:history WHERE id = :id")
    suspend fun updateHistory(history: String, id: Int)

    @Query("UPDATE habit_table SET record=:record WHERE id = :id")
    suspend fun updateRecord(record: String, id: Int)

    @Query("UPDATE habit_table SET allDays=:allDays WHERE id = :id")
    suspend fun updateAllDays(allDays: Int, id: Int)

    @Query("UPDATE habit_table SET attempts = :attempts WHERE id = :id")
    suspend fun updateAttempts(attempts: Int, id: Int)

    @Query("SELECT * FROM habit_table WHERE id = (SELECT MAX(id) FROM habit_table)")
    fun getLastHabit(): Flow<HabitModel>
}