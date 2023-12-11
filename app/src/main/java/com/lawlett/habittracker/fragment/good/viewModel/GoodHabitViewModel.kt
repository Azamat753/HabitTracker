package com.lawlett.habittracker.fragment.good.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.models.BadHabitModel
import com.lawlett.habittracker.models.GoodHabitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GoodHabitViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val habitFlow = MutableSharedFlow<List<GoodHabitModel>>()

    fun insert(goodHabitModel: GoodHabitModel) {
        viewModelScope.launch {
            repository.insertGoodHabit(goodHabitModel)
        }
    }

    fun updateAllDays(allDays: Int, id: Int) {
        viewModelScope.launch {
            repository.updateGoodHabitAllDays(allDays, id)
        }
    }

    fun updateCurrentDay(currentDay: Int,lastDate:Date, id: Int) {
        viewModelScope.launch {
            repository.updateGoodHabitCurrentDay(currentDay,lastDate, id)
        }
    }

    fun delete(goodHabitModel: GoodHabitModel) {
        viewModelScope.launch {
            repository.deleteGoodHabit(goodHabitModel)
        }
    }

    fun getHabits() {
        viewModelScope.launch {
            repository.getGoodHabits()
                .flowOn(Dispatchers.IO).onEach { it ->
                    it.forEach {
                        var currentDays = it.currentDay?:0
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