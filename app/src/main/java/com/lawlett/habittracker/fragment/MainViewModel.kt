package com.lawlett.habittracker.fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val habitFlow = MutableSharedFlow<List<HabitModel>>(
        replay = 2
    )

    fun insert(habitModel: HabitModel) {
        viewModelScope.launch {
            repository.insert(habitModel)
        }
    }

    fun delete(habitModel: HabitModel) {
        viewModelScope.launch {
            repository.delete(habitModel)
        }
    }

    fun getHabits() {
        viewModelScope.launch {
            repository.getHabits()
                .flowOn(Dispatchers.IO).onEach {
                    habitFlow.emit(it)
                }.launchIn(viewModelScope)
        }
    }


}