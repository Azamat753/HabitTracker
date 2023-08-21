package com.lawlett.habittracker.fragment.habitdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
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
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

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
    private var isAddedToHistory = false

    @Inject
    lateinit var dao: HabitDao

    private lateinit var timerManager: TimerManager
    var listHistory = arrayListOf<String>()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepare()
        if (!viewModel.isUserSeen()) {
            searchlight()
        }
        data?.id?.let { id ->
            viewModel.getHistory(id)
        }
        getHistory()
        initAdapter()
        observe()
        initClickers()
    }

    private fun searchlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.second_target, root)
        val view = View(requireContext())

        Handler().postDelayed({
            viewModel.saveUserSeen()
            val views = setSpotLightTarget(
                binding.minaDetail,
                first,
                getString(R.string.detail_display)
            )

            val zeroStop = setSpotLightTarget(
                binding.habitTv,
                first,
                getString(R.string.detail_habit_name_habit)
            )

            val nameStop = setSpotLightTarget(
                binding.nameTv,
                first,
                getString(R.string.detail_habit_name)
            )

            val firstSpot = setSpotLightTarget(
                binding.habitProgress,
                first,
                getString(R.string.detail_habit_progress)
            )
            val secondSpot = setSpotLightTarget(
                binding.timeTV,
                first,
                getString(R.string.detail_habit_time)
            )
            val thirdSpot = setSpotLightTarget(
                binding.btnRelapse,
                first,
                getString(R.string.detail_habit_btn_relapse)
            )

            val fourSpot = setSpotLightTarget(
                binding.tvAttempts,
                first,
                getString(R.string.detail_habit_attempts)
            )
            val fiveSpot = setSpotLightTarget(
                binding.tvRecord,
                first,
                getString(R.string.detail_habit_record)
            )

            val sixStop = setSpotLightTarget(
                binding.recyclerHistory,
                first,
                getString(R.string.detail_habit_history_list)
            )

            targets.add(views)
            targets.add(zeroStop)
            targets.add(nameStop)
            targets.add(firstSpot)
            targets.add(secondSpot)
            targets.add(thirdSpot)
            targets.add(fourSpot)
            targets.add(fiveSpot)
            targets.add(sixStop)
            setSpotLightBuilder(requireActivity(), targets, first)
        }, 100)

    }

    private fun getHistory() {
        data?.id?.let { id ->
            viewModel.getHistory(id)
        }
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
            showRecord()
            timerManager.resetAction()
            timerManager.startStopAction(isFollow, data?.startDate, data?.endDate)
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    private fun showRecord() {
        val nowRecord = binding.recordTv.text.toString().ifEmpty { "0" }.toInt()
        val newRecord = dataHelper.startTimeFromPref()?.getDays().toString().toInt()
        if (newRecord > nowRecord) {
            binding.recordTitleTv.toVisible()
            binding.recordTv.toVisible()
            binding.recordTv.text = newRecord.toString()
            data?.id?.let {
                viewModel.updateRecord(newRecord.toString(), data?.id!!)
            } ?: kotlin.run {
                showToast("id пуст")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun launch() {
        timerManager = TimerManager(dataHelper, binding)
        addAttempts()
        updateHistory()
        data?.let {
            val model = HabitModel(
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
        timerManager.startStopAction(isFollow, data?.startDate, data?.endDate)
    }

    @SuppressLint("SetTextI18n")
    private fun prepare() {
        if (arguments != null) {
            data = requireArguments().getParcelable("key") as HabitModel?
            isFollow = requireArguments().getBoolean("isFollow")
            isStartTimer = requireArguments().getBoolean("isStartTimer")
            data?.let { model ->
                with(binding) {
                    iconTv.text = model.icon
                    habitProgress.max = model.allDays
                    habitTv.text = data?.title
                    recordTv.text = data?.record
                    viewModel.record = data?.attempts ?: 0
                    if (data?.record?.toInt() == 0 || data?.record==null) {
                        recordTv.toGone()
                        recordTitleTv.toGone()
                    } else {
                        recordTv.text = data?.record
                    }
                    if (data?.attempts == 0) {
                        attemptCard.toGone()
                    } else {
                        tvAttempts.text =
                            "${getString(R.string.tv_attempt)} ${data?.attempts.toString()}"
                    }
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
                    habitProgress.progress = dataHelper.startTimeFromPref()?.getDays()?.toInt() ?: 0
                }
            }
        }
        timerManager = TimerManager(dataHelper, binding)

        if (dataHelper.timerCounting()) {
            timerManager.startTimer()
        }
        // todo else {
//            timerManager.stopTimer()
//            dataHelper.startTime()?.let { startTime ->
//                dataHelper.stopTime()?.let { stopTime ->
//                    val time = Date().time - (startTime.time - stopTime.time)
//                    binding.timeTV.text = timerManager.timeStringFromLong(time)
//                }
//            }
//        }
        timer.scheduleAtFixedRate(TimeTask(), 0, 500)

        if (isStartTimer) {
            timerManager.startStopAction()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addAttempts() {
        binding.attemptCard.toVisible()
        viewModel.record()
        viewModel.attemptsNumber.observe(requireActivity()) {
            it?.let { attempts ->
                binding.tvAttempts.text = getString(R.string.tv_attempt) + " " + attempts
                viewModel.updateAttempts(attempts, data?.id!!)
            }
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitFlow.asSharedFlow().collect() { historyDB ->
                    listHistory = historyToArray(historyDB)
                    adapter.setData(listHistory)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun updateHistory() {
        viewModel.getTime()
        viewModel.date.observe(viewLifecycleOwner) { newHistory ->
            listHistory.add(newHistory)
            isAddedToHistory = true
        }
        data?.let {
            val model = HabitModel(
                id = it.id,
                history = historyArrayToJson(listHistory),
                allDays = it.allDays,
                title = it.title,
                currentDay = it.currentDay,
                icon = it.icon,
                record = dataHelper.startTimeFromPref()?.getDays()
            )
            if (isAddedToHistory) {
                viewModel.update(model)
                observe()
            }
        }
    }

    private fun onClick(historyString: String, position: Int) {
        listHistory.remove(historyString)
        data?.let {
            val model = HabitModel(
                id = it.id,
                history = historyArrayToJson(listHistory),
                allDays = it.allDays,
                title = it.title,
                currentDay = it.currentDay,
                icon = it.icon
            )
            viewModel.update(model)
            observe()
        }
        adapter.notifyItemRemoved(position)
        Toast.makeText(requireContext(), historyString, Toast.LENGTH_SHORT).show()
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