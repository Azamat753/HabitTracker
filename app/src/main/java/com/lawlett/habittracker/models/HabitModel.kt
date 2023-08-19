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
    val allDays: String?,
    val currentDay:Int?=0,
   @ColumnInfo(name = "history_array") var history:ArrayList<String>,
    val attempts: Int? = 0,
    val record: Int? = 0,
    val fbName : String?=null,
    val startDate: Date?=null,
    val endDate: Date?=null
) : Parcelable{

}
