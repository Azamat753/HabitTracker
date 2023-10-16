package com.lawlett.habittracker.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "good_habit_table")
data class GoodHabitModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String?,
    val icon: String?,
    val allDays: Int = 7,
    val currentDay: Int? = 0,
    val fbName: String? = null,
    var lastDate: Date? = null,
) : Parcelable