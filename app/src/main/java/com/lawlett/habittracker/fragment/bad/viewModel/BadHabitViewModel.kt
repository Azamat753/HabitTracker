package com.lawlett.habittracker.fragment.bad.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.models.BadHabitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadHabitViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val habitFlow = MutableSharedFlow<List<BadHabitModel>>()

    val lastHabitFlow = MutableSharedFlow<BadHabitModel>()

    fun insert(badHabitModel: BadHabitModel) {
        viewModelScope.launch {
            repository.insert(badHabitModel)
        }
    }

    fun updateAllDays(allDays: Int, id: Int) {
        viewModelScope.launch {
            repository.updateAllDays(allDays, id)
        }
    }

    fun delete(badHabitModel: BadHabitModel) {
        viewModelScope.launch {
            repository.delete(badHabitModel)
        }
    }

    fun getLastHabit() {
        viewModelScope.launch {
            repository.getLastHabit().
            flowOn(Dispatchers.IO).onEach {
                lastHabitFlow.emit(it)
            }.launchIn(viewModelScope)
        }
    }

    fun getHabits() {
        viewModelScope.launch {
            repository.getHabits()
                .flowOn(Dispatchers.IO).onEach {
                    it.forEach {
                        var currentDays = it.startDate?.getDays()?.toInt()?:0
                        val daysFromRoom = it.allDays

                        if (currentDays >= daysFromRoom){
                            currentDays = it.allDays + 7
                            it.id?.let {id->
                                updateAllDays(currentDays,id)
                            }
                        }
                    }
                    habitFlow.emit(it)
                }.launchIn(viewModelScope)
        }
    }
}