package com.lawlett.habittracker.fragment.habitdetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding
import com.lawlett.habittracker.fragment.habitdetail.adapter.HabitDetailAdapter
import com.lawlett.habittracker.fragment.habitdetail.viewmodel.HabitDetailViewModel
import com.lawlett.habittracker.helper.DataHelper
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.TimerManager
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.toGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail) {

    private val binding: FragmentHabitDetailBinding by viewBinding()
    private val adapter = HabitDetailAdapter(this::onClick)
    private val viewModel: HabitDetailViewModel by viewModels()
    private val list = arrayListOf<HabitModel>()
    lateinit var dataHelper: DataHelper
    private val timer = Timer()
    private var data: HabitModel? = null
    private var isFollow = false
    private lateinit var timerManager: TimerManager
    @Inject lateinit var firebaseHelper: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepare()
        initClickers()
    }

    private fun initClickers() {
        binding.btnRelapse.setOnClickListener {
            binding.tvRecord.text = "Рекорд - " + binding.timeTV.text.substring(0, 2) + " дней"
            addAttempts()
            addDate()
            data?.let {
                val model = HabitModel(
                    date = timerManager.time().toString(),
                    title = it.title,
                    allDays = it.allDays,
                    currentDay = it.currentDay,
                    icon = it.icon,
                    startDate = dataHelper.startTime(),
                    endDate = dataHelper.stopTime()
                )
                firebaseHelper.insertOrUpdateHabitFB(model)
            }
            timerManager.startStopAction()
        }
    }

    private fun prepare() {
        if (arguments != null) {
            data = requireArguments().getParcelable("key") as HabitModel?
            isFollow = requireArguments().getBoolean("isFollow")
            data?.let { model ->
                binding.tvIcon.text = model.icon
                binding.habitProgress.max = model.allDays?.toInt() ?: 0
                binding.habitProgress.progress = model.currentDay ?: 0
                if (isFollow) {
                    dataHelper =
                        DataHelper(
                            requireActivity(),
                            "${model.title} start ${model.fbName}",
                            "${model.title} stop ${model.fbName}"
                        )
                    dataHelper.setTimerCounting(true)
                    binding.btnRelapse.toGone()
                } else {
                    dataHelper =
                        DataHelper(requireActivity(), "${model.title} start", "${model.title} stop")
                }

            }
        }
        timerManager = TimerManager(dataHelper, binding)

        if (dataHelper.timerCounting()) {
            timerManager.startTimer()
        } else {
            timerManager.stopTimer()
            dataHelper.startTime()?.let { startTime ->
                dataHelper.stopTime()?.let { stopTime ->
                    val time = Date().time - (startTime.time - stopTime.time)
                    binding.timeTV.text = timerManager.timeStringFromLong(time)
                }
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

    inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    timerManager.updateTime(isFollow, data?.startDate)
                }
            }
        }
    }

}