package com.lawlett.habittracker.fragment.habitdetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawlett.habittracker.Repository
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.models.NotificationModel
import com.lawlett.habittracker.models.TokenModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val habitFlow = MutableSharedFlow<String>()

    val isNotificationPushed = MutableSharedFlow<Boolean>()

    val tokenModelFlow = MutableSharedFlow<TokenModel>()


    private val dateTime = MutableLiveData<String>()
    val date: LiveData<String>
        get() = dateTime

    private val attemptsNumbers = MutableLiveData<Int>()
    val attemptsNumber: LiveData<Int>
        get() = attemptsNumbers

    var record = 0
    fun update(model: HabitModel) {
        viewModelScope.launch {
            repository.update(model)
        }
    }

    fun getToken(authCode: String) {
        viewModelScope.launch {
            tokenModelFlow.emit(repository.getToken(authCode))
        }
    }

    fun sendRemoteNotification(notificationModel: NotificationModel, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                repository.sendRemoteNotification(notificationModel, token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateRecord(record: String, id: Int) {
        viewModelScope.launch {
            repository.updateRecord(record, id)
        }
    }

    fun updateHistory(history: String, id: Int) {
        viewModelScope.launch {
            repository.updateHistory(history, id)
        }
    }

    fun updateAttempts(attempts: Int, id: Int) {
        viewModelScope.launch {
            delay(100)
            repository.updateAttempts(attempts, id)
        }
    }

    fun getHistory(id: Int) {
        viewModelScope.launch {
            repository.getHistory(id)
                .flowOn(Dispatchers.IO).onEach {
                    habitFlow.emit(it)
                }.launchIn(viewModelScope)
        }
    }


    fun getTime(): String {
        val i = Calendar.getInstance()
        val a = SimpleDateFormat("dd MM yyyy г. HH:mm:ss", Locale.getDefault())
        return a.format(i.time)
    }

    fun addAttempt() {
        record++
        attemptsNumbers.value = record
    }

    fun minusAttempt() {
        record--
        attemptsNumbers.value = record
    }
}