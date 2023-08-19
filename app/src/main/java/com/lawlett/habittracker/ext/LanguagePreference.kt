package com.lawlett.habittracker.ext

import android.content.Context
import android.content.SharedPreferences



class LanguagePreference(context: Context) {
    private val preferences: SharedPreferences
    val getLanguage: String?
        get() = preferences.getString("language_", "")

    fun saveLanguage(s: String) {
        preferences.edit().putString("language_", s).apply()
    }

    companion object {
        @Volatile
        var instance: LanguagePreference? = null
        fun getInstance(context: Context): LanguagePreference? {
            if (instance == null) LanguagePreference(context)
            return instance
        }
    }

    init {
        instance = this
        preferences = context.getSharedPreferences("my_language", Context.MODE_PRIVATE)
    }
}
