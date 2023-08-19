package com.lawlett.habittracker.fragment.habitdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.DialogRelapseBinding
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding
import com.lawlett.habittracker.ext.createDialog
import com.lawlett.habittracker.ext.historyArrayToJson
import com.lawlett.habittracker.ext.historyToArray
import com.lawlett.habittracker.fragment.habitdetail.adapter.HabitDetailAdapter
import com.lawlett.habittracker.fragment.habitdetail.viewmodel.HabitDetailViewModel
import com.lawlett.habittracker.helper.DataHelper
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.TimerManager
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.ext.toGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail) {

    private val binding: FragmentHabitDetailBinding by viewBinding()
    private val adapter = HabitDetailAdapter(this::onClick)
    private val viewModel: HabitDetailViewModel by viewModels()
    lateinit var dataHelper: DataHelper
    private val timer = Timer()
    private var data: HabitModel? = null
    private var isFollow = false
    private var isStartTimer = false
    private var isAdded = false

    private lateinit var timerManager: TimerManager
    var listHistory = arrayListOf<String>()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepare()
        data?.id?.let {id->
            viewModel.getHistory(id)
        }
        initAdapter()
        observe()
        initClickers()
    }

    private fun initAdapter() {
        binding.recyclerHistory.adapter = adapter
    }

    private fun initClickers() {
        binding.btnRelapse.setOnClickListener {
            dialogRelapse()
        }

        binding.appBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun dialogRelapse() {
        val dialog = requireContext().createDialog(DialogRelapseBinding::inflate)
        dialog.first.btnYes.setOnClickListener {
            launch()
            timerManager.resetAction()
            timerManager.startTimer()
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    private fun launch() {
        binding.tvRecord.text =
            "${R.string.tv_record}" + binding.timeTV.text.substring(0, 2) + " ${R.string.day}"
        addAttempts()
        addDate()
        data?.let {
            val model = HabitModel(
//                date = timerManager.time().toString(),
                title = it.title,
                allDays = it.allDays,
                currentDay = it.currentDay,
                icon = it.icon,
                startDate = dataHelper.startTime(),
                endDate = dataHelper.stopTime(),
                history = it.history
            )
            firebaseHelper.insertOrUpdateHabitFB(model)
        }
        timerManager.startStopAction()
    }

    private fun prepare() {
        if (arguments != null) {
            data = requireArguments().getParcelable("key") as HabitModel?
            isFollow = requireArguments().getBoolean("isFollow")
            isStartTimer = requireArguments().getBoolean("isStartTimer")
            data?.let { model ->
                with(binding) {
                    tvIcon.text = model.icon
                    habitProgress.max = model.allDays?.toInt() ?: 0
                    habitProgress.progress = model.currentDay ?: 0
                    habitTv.text = data?.title
                    if (isFollow) {
                        dataHelper =
                            DataHelper(
                                requireActivity(),
                                "${model.title} start ${model.fbName}",
                                "${model.title} stop ${model.fbName}"
                            )
                        dataHelper.setTimerCounting(true)
                        nameTv.text = data?.fbName?.replaceAfter(":", "")
                        btnRelapse.toGone()
                    } else {
                        dataHelper =
                            DataHelper(
                                requireActivity(),
                                "${model.title} start",
                                "${model.title} stop"
                            )
                    }
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

        if (isStartTimer) {
            timerManager.startStopAction()
        }
    }

    private fun addAttempts() {
        viewModel.record()
        viewModel.attemptsNumber.observe(requireActivity()) {
            binding.tvAttempts.text = getString(R.string.tv_attempt) + " " + it
        }
    }

    fun observe(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitFlow.asSharedFlow().collect() { historyDB->
                    listHistory = historyDB as ArrayList<String>
                    adapter.setData(listHistory)
                }
            }
        }
    }

    private fun addDate() {
        viewModel.getTime()
        viewModel.date.observe(viewLifecycleOwner) { newHistory->
            listHistory.add(newHistory)
            isAdded = true
        }
        data?.let {
            val model = HabitModel(
                id = it.id,
                history  = listHistory,
                allDays = it.allDays,
                title = it.title,
                currentDay = it.currentDay,
                icon = it.icon)
            if (isAdded){
                viewModel.update(model)
                viewModel.getHistory(it.id!!)
            }
        }
    }

    private fun onClick(habitModel: String) {
        //   list.remove(habitModel)
        Toast.makeText(requireContext(), habitModel, Toast.LENGTH_SHORT).show()
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