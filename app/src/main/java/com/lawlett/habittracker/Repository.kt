package com.lawlett.habittracker

import com.lawlett.habittracker.api.FirebaseApi
import com.lawlett.habittracker.api.SignApi
import com.lawlett.habittracker.models.FirebaseResponse
import com.lawlett.habittracker.models.BadHabitModel
import com.lawlett.habittracker.models.GoodHabitModel
import com.lawlett.habittracker.models.NotificationModel
import com.lawlett.habittracker.room.BadHabitDao
import com.lawlett.habittracker.room.GoodHabitDao
import java.util.Date
import javax.inject.Inject


class Repository @Inject constructor(
    private val badHabitDao: BadHabitDao,
    private val goodHabitDao: GoodHabitDao,
    private val api: FirebaseApi,
    private val signApi: SignApi
) {

    suspend fun insertGoodHabit(goodHabitModel: GoodHabitModel) {
        goodHabitDao.insert(goodHabitModel)
    }

    fun getGoodHabits() = (goodHabitDao.getAll())

    suspend fun updateGoodHabitAllDays(allDays: Int, id: Int) {
        goodHabitDao.updateAllDays(allDays, id)
    }

    suspend fun updateGoodHabitCurrentDay(currentDay: Int,lastDate: Date, id: Int) {
        goodHabitDao.updateCurrentDay(currentDay, lastDate,id)
    }

    suspend fun deleteGoodHabit(goodHabitModel: GoodHabitModel) {
        goodHabitDao.delete(goodHabitModel)
    }

    suspend fun insert(badHabitModel: BadHabitModel) {
        badHabitDao.insert(badHabitModel)
    }

    suspend fun update(badHabitModel: BadHabitModel) {
        badHabitDao.update(badHabitModel)
    }

    suspend fun delete(badHabitModel: BadHabitModel) {
        badHabitDao.delete(badHabitModel)
    }

    fun getHabits() = (badHabitDao.getAll())

    fun getLastHabit() = badHabitDao.getLastHabit()

    fun getHistory(id: Int) = badHabitDao.getHistory(id)
    suspend fun updateRecord(record: String, id: Int) {
        badHabitDao.updateRecord(record, id)
    }

    suspend fun updateHistory(history: String, id: Int) {
        badHabitDao.updateHistory(history, id)
    }


    suspend fun sendRemoteNotification(
        notificationModel: NotificationModel,
        token: String
    ): FirebaseResponse? {
        return api.sendRemoteNotification(notificationModel, "Bearer $token")
    }

    suspend fun getToken(code: String) =
        signApi.getToken(code = code)

    suspend fun updateAllDays(allDays: Int, id: Int) {
        badHabitDao.updateAllDays(allDays, id)
    }

    suspend fun updateAttempts(attempts: Int, id: Int) {
        badHabitDao.updateAttempts(attempts, id)
    }

}