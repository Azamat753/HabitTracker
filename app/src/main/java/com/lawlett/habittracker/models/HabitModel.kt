package com.lawlett.habittracker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "habit_table")
data class HabitModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String?,
    val icon: String?,
    val allDays: String?,
    val currentDay:Int?=0,
    val date:String?= null,
    val attempts: Int? = 0,
    val record: Int? = 0,
//    val history: ArrayList<String>
) : Parcelable