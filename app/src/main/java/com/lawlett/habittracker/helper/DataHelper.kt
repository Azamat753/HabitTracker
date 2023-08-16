package com.lawlett.habittracker.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.lawlett.habittracker.databinding.ActivityMainBinding
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding
import com.lawlett.habittracker.fragment.habitdetail.HabitDetailFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DataHelper(context: Context, startKey: String, stopKey: String) {
    private val PREFERENCES = "prefs"
    private val START_TIME_KEY = startKey
    private val STOP_TIME_KEY = stopKey
    private val COUNTING_KEY = "$startKey counting"
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

    private var timerCounting = false
    private var startTime: Date? = null
    private var stopTime: Date? = null

    init {
        timerCounting = sharedPref.getBoolean(COUNTING_KEY, false)

        val startString = sharedPref.getString(START_TIME_KEY, null)
        if (startString != null)
            startTime = dateFormat.parse(startString)

        val stopString = sharedPref.getString(STOP_TIME_KEY, null)
        if (stopString != null)
            stopTime = dateFormat.parse(stopString)
    }


    fun startTime(): Date? = startTime

    fun setStartTime(date: Date?) {
        startTime = date
        with(sharedPref.edit())
        {
            val stringDate = if (date == null) null else dateFormat.format(date)
            putString(START_TIME_KEY, stringDate)
            apply()
        }
    }

    fun stopTime(): Date? = stopTime

    fun setStopTime(date: Date?) {
        stopTime = date
        with(sharedPref.edit())
        {
            val stringDate = if (date == null) null else dateFormat.format(date)
            putString(STOP_TIME_KEY, stringDate)
            apply()
        }
    }

    fun timerCounting(): Boolean = timerCounting

    fun setTimerCounting(value: Boolean) {
        timerCounting = value
        with(sharedPref.edit())
        {
            putBoolean(COUNTING_KEY, value)
            apply()
        }
    }
}

// TimerManager.kt
class TimerManager(
    private val dataHelper: DataHelper,
    private val binding: FragmentHabitDetailBinding
) {

    fun startTimer() {
        dataHelper.setTimerCounting(true)
        //      binding.startButton.text = binding.root.context.getString(R.string.stop)
    }

    fun stopTimer() {
        dataHelper.setTimerCounting(false)
        // binding.startButton.text = binding.root.context.getString(R.string.start)
    }

    fun resetAction() {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.timeTV.text = timeStringFromLong(0)
    }

    fun startStopAction() {
        if (dataHelper.timerCounting()) {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else {
            if (dataHelper.stopTime() != null) {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    fun updateTime(isFollow: Boolean = false, startTime: Date? = null) {
        if (dataHelper.timerCounting()) {
            val time = Date().time - (if (isFollow) startTime?.time?:0 else  dataHelper.startTime()?.time ?: 0)
            binding.timeTV.text = timeStringFromLong(time)
        }
    }

    fun time() = Date().time - (dataHelper.startTime()?.time ?: 0)


    private fun calcRestartTime(): Date {
        val diff = (dataHelper.startTime()?.time ?: 0) - (dataHelper.stopTime()?.time ?: 0)
        return Date(System.currentTimeMillis() + diff)
    }

    fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        val days = (ms / (1000 * 60 * 60 * 24))
        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
    }
}