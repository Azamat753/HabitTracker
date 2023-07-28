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
}