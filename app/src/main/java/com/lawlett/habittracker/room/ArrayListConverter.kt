package com.lawlett.habittracker.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.lawlett.habittracker.ext.fromJson

object ArrayListConverter {

    @TypeConverter
    fun toStringArrayList(value: String): ArrayList<String> {
        return try {
            Gson().fromJson<ArrayList<String>>(value) //using extension function
        } catch (e: Exception) {
            arrayListOf()
        }
    }


    @TypeConverter
    fun fromStringArrayList(value: ArrayList<String>?=null): String {
        return Gson().toJson(value)
    }

}