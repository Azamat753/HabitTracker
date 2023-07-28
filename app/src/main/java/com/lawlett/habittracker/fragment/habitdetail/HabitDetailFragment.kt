package com.lawlett.habittracker.fragment.habitdetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding
import com.lawlett.habittracker.fragment.habitdetail.adapter.HabitDetailAdapter
import com.lawlett.habittracker.fragment.habitdetail.viewmodel.HabitDetailViewModel
import com.lawlett.habittracker.helper.DataHelper
import com.lawlett.habittracker.models.HabitModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail),
    BaseAdapter.IBaseAdapterClickListener<HabitModel> {

    private val binding: FragmentHabitDetailBinding by viewBinding()
    private val adapter = HabitDetailAdapter(this::onClick)
    private val viewModel: HabitDetailViewModel by viewModels()
    private val list = arrayListOf<HabitModel>()
    lateinit var dataHelper: DataHelper
    private val timer = Timer()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.listener = this
        dataHelper = DataHelper(requireActivity())

        binding.btnRelapse.setOnClickListener {
            addAttempts()
            addDate()
            startStopAction()
        }
        binding.btnSaveData.setOnClickListener {
            stopTimer()
        }
        if (dataHelper.timerCounting()) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.timeTV.text = timeStringFromLong(time)
            }
        }

        timer.scheduleAtFixedRate(TimeTask(), 0, 500)
    }

    private fun addAttempts() {
        viewModel.record()
        viewModel.attemptsNumber.observe(requireActivity()) {
            binding.tvAttempts.text = "Попытка - ${it}"
        }
    }

    private fun addDate() {
        viewModel.getTime()
        viewModel.date.observe(viewLifecycleOwner) {
            list.add(
                HabitModel(
                    date = it, allDays = null, title = null, currentDay = null, icon = null
                )
            )
            adapter.setData(list)
            binding.recyclerHistory.adapter = adapter
            viewModel.date.removeObservers(viewLifecycleOwner)
        }
    }

    private fun onClick(habitModel: HabitModel) {
        list.remove(habitModel)
        Toast.makeText(requireContext(), habitModel.date, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(model: HabitModel, position: Int) {
        list.remove(model)
        Toast.makeText(requireContext(), "ololo", Toast.LENGTH_SHORT).show()
    }
    private inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                val time = Date().time - dataHelper.startTime()!!.time
                lifecycleScope.launch(Dispatchers.Main){
                    binding.timeTV.text = timeStringFromLong(time)
                }
            }
        }
    }

    private fun resetAction() {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.timeTV.text = timeStringFromLong(0)
    }

    private fun stopTimer() {
        dataHelper.setTimerCounting(false)
      //  binding.startButton.text = getString(R.string.start)
    }

    private fun startTimer() {
        dataHelper.setTimerCounting(true)
     //   binding.startButton.text = getString(R.string.stop)
    }

    private fun startStopAction() {
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

    private fun calcRestartTime(): Date {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        val days = (ms / (1000 * 60 * 60 * 24))
        return makeTimeString(days,hours, minutes, seconds)
    }

    private fun makeTimeString(days: Long,hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d:%02d",days, hours, minutes, seconds)
    }
}