package com.lawlett.habittracker.fragment.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val habitFlow = MutableSharedFlow<List<HabitModel>>()

    val lastHabitFlow = MutableSharedFlow<HabitModel>()

    fun insert(habitModel: HabitModel) {
        viewModelScope.launch {
            repository.insert(habitModel)
        }
    }

    fun updateAllDays(allDays: Int, id: Int) {
        viewModelScope.launch {
            repository.updateAllDays(allDays, id)
        }
    }

    fun delete(habitModel: HabitModel) {
        viewModelScope.launch {
            repository.delete(habitModel)
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