package com.lawlett.habittracker

import android.content.SharedPreferences
import com.lawlett.habittracker.api.FirebaseApi
import com.lawlett.habittracker.api.SignApi
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.models.FirebaseResponse
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.models.NotificationModel
import com.lawlett.habittracker.room.HabitDao
import javax.inject.Inject


class Repository @Inject constructor(
    private val dao: HabitDao,
    private val pref: SharedPreferences,
    private val api: FirebaseApi,
    private val cacheManager: CacheManager,
    private val signApi: SignApi
) {

    fun isUserSeen(): Boolean {
        return pref.getBoolean(KEY_BORD, false)
    }

    fun saveUserSeen() {
        pref.edit().putBoolean(KEY_BORD, true).apply()
    }

    fun isLangeSeen(): Boolean {
        return pref.getBoolean(KEY_LANGE, false)
    }

    fun saveLangeSeen() {
        pref.edit().putBoolean(KEY_LANGE, true).apply()
    }

    suspend fun insert(habitModel: HabitModel) {
        dao.insert(habitModel)
    }

    suspend fun update(habitModel: HabitModel) {
        dao.update(habitModel)
    }

    suspend fun delete(habitModel: HabitModel) {
        dao.delete(habitModel)
    }

    fun getHabits() = (dao.getAll())

    fun getLastHabit() = dao.getLastHabit()

    fun getHistory(id: Int) = dao.getHistory(id)
    suspend fun updateRecord(record: String, id: Int) {
        dao.updateRecord(record, id)
    }

    suspend fun updateHistory(history: String, id: Int) {
        dao.updateHistory(history, id)
    }


    suspend fun sendRemoteNotification(
        notificationModel: NotificationModel,
        token: String
    ): FirebaseResponse? {
        return api.sendRemoteNotification(notificationModel, "Bearer $token")
    }

    suspend fun getToken(code: String) =
        signApi.getToken(code = code)


    companion object {
        const val KEY_PREF = "pref"
        const val KEY_BORD = "bord"
        const val KEY_LANGE = "LANGE"
    }

    suspend fun updateAllDays(allDays: Int, id: Int) {
        dao.updateAllDays(allDays, id)
    }

    suspend fun updateAttempts(attempts: Int, id: Int) {
        dao.updateAttempts(attempts, id)
    }

}