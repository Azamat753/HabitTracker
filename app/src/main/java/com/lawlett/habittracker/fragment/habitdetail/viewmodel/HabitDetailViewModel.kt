package com.lawlett.habittracker.fragment.habitdetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitDetailViewModel() : ViewModel() {

    private val dateTime = MutableLiveData<String>()
    val date:LiveData<String>
        get() = dateTime

    private val attemptsNumbers = MutableLiveData<Int>()
    val attemptsNumber: LiveData<Int>
        get() = attemptsNumbers

    var record = 0

    fun getTime(): LiveData<String> {
        val i = Calendar.getInstance()
        val a = SimpleDateFormat("dd MM yyyy г. HH:mm", Locale.getDefault())
        dateTime.postValue(a.format(i.time))
        return date
    }

    fun record() {
        record++
        attemptsNumbers.value =record
    }
}