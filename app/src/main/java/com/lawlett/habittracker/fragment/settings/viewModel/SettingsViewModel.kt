package com.lawlett.habittracker.fragment.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.helper.FirebaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository,val firebaseHelper: FirebaseHelper) : ViewModel() {

    val completeFlow = MutableSharedFlow<Boolean>()

    fun sync() {
        viewModelScope.launch {
            repository.getHabits()
                .flowOn(Dispatchers.IO).onEach {
                    firebaseHelper.deleteAll()
                    delay(3000)
                    it.forEach {
                        firebaseHelper.insertOrUpdateHabitFB(it)
                    }
                    completeFlow.emit(false)
                }.launchIn(viewModelScope)
        }
    }
}