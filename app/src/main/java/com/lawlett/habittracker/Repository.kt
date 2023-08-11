package com.lawlett.habittracker

import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.room.HabitDao
import javax.inject.Inject


class Repository @Inject constructor(private val dao: HabitDao) {

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
}