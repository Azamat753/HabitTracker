package com.lawlett.habittracker.fragment.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
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
//    fun isLangeSeen(): Boolean {
//        return repository.isLangeSeen()
//    }
//
//    fun saveLangeSeen() {
//        repository.saveLangeSeen()
//    }

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