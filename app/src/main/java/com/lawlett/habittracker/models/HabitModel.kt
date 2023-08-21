package com.lawlett.habittracker.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.lawlett.habittracker.ext.fromJson
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "habit_table")
data class HabitModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String?,
    val icon: String?,
    val allDays: Int=7,
    val currentDay: Int = 0,
    var history: String?=null,
    val attempts: Int = 0,
    val record: String?=null,
    val fbName: String? = null,
    val startDate: Date? = null,
    val endDate: Date? = null
) : Parcelable {

}
