package com.lawlett.habittracker.helper

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.lawlett.habittracker.helper.Key.KEY_FOR_COLOR
import com.lawlett.habittracker.helper.Key.KEY_SAVE_DIALOG
import java.lang.reflect.Type
import java.util.ArrayList

class CacheManager(context: Context) {

    private var sharedPreferences = context.getSharedPreferences("habitPref", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        var instance: CacheManager? = null
        fun getInstance(context: Context): CacheManager? {
            if (instance == null) CacheManager(context)
            return instance
        }
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key,0)
    }

    fun saveInt(key: String, s: Int) {
        sharedPreferences.edit().putInt(key, s).apply()
    }



    fun saveColor(color: Int) = sharedPreferences.edit().putInt(KEY_FOR_COLOR, color).apply()

    fun getColor() = sharedPreferences.getInt(KEY_FOR_COLOR, 0)


    fun setToken(token: String) = sharedPreferences.edit().putString("token", token).apply()

    fun getToken(): String? = sharedPreferences.getString("token", null)
    fun isUserFollow(): Boolean {
        return sharedPreferences.getBoolean(Key.KEY_SAVE_FOLLOW, false)
    }

    fun saveUserFollow() {
        sharedPreferences.edit().putBoolean(Key.KEY_SAVE_FOLLOW, true).apply()
    }

    fun isUserSetting(): Boolean {
        return sharedPreferences.getBoolean(Key.KEY_SAVE_SETTINGS, false)
    }

    fun saveSettings() {
        sharedPreferences.edit().putBoolean(Key.KEY_SAVE_SETTINGS, true).apply()
    }

    fun isUserSeen(): Boolean {
        return sharedPreferences.getBoolean(Key.KEY_BORD, false)
    }

    fun saveUserSeen() {
        sharedPreferences.edit().putBoolean(Key.KEY_BORD, true).apply()
    }

    fun isLangeSeen(): Boolean {
        return sharedPreferences.getBoolean(Key.KEY_LANGE, false)
    }

    fun saveLangeSeen() {
        sharedPreferences.edit().putBoolean(Key.KEY_LANGE, true).apply()
    }

    fun isUserSeenDialog(): Boolean {
        return sharedPreferences.getBoolean(KEY_SAVE_DIALOG, false)
    }

    fun saveUserSeenDialog() {
        sharedPreferences.edit().putBoolean(KEY_SAVE_DIALOG, true).apply()
    }


    fun saveInstruction(isPass: Boolean) {
        sharedPreferences.edit().putBoolean("isPass", isPass).apply()
    }

    fun isPass(): Boolean {
        return sharedPreferences.getBoolean("isPass", false)
    }

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