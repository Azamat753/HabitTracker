package com.lawlett.habittracker

import android.content.SharedPreferences
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.room.HabitDao
import javax.inject.Inject


class Repository @Inject constructor(private val dao: HabitDao,private val pref:SharedPreferences) {

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

//    override fun getUsers(): Flow<List<User>> = flow {
//        emit(appDatabase.userDao().getAll())
//    }
//
//    override fun insertAll(users: List<User>): Flow<Unit> = flow {
//        appDatabase.userDao().insertAll(users)
//        emit(Unit)
//    }

    suspend fun insert(habitModel: HabitModel) {
        dao.insert(habitModel)
    }

    suspend fun update(habitModel: HabitModel) {
        dao.update(habitModel)
    }

    suspend fun delete(habitModel: HabitModel) {
        dao.delete(habitModel)
    }
    fun getHabits() =(dao.getAll())

    fun getHistory(id:Int) = dao.getHistory(id)

    companion object {
        const val KEY_PREF = "pref"
        const val KEY_BORD = "bord"
        const val KEY_LANGE = "LANGE"
    }

}