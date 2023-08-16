package com.lawlett.habittracker.helper

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.ArrayList

class CacheManager(context: Context) {

    private var sharedPreferences = context.getSharedPreferences("habitPref", Context.MODE_PRIVATE)

    fun saveFollowers(list: ArrayList<String?>) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        sharedPreferences.edit().putString("followers", json).apply()
    }

    fun getFollowers(): ArrayList<String?>? {
        val gson = Gson()
        val json: String? = sharedPreferences.getString("followers", null)
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }
}