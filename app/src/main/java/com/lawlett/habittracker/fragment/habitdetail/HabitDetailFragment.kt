package com.lawlett.habittracker.fragment.habitdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding
import com.lawlett.habittracker.ext.*
import com.lawlett.habittracker.fragment.habitdetail.adapter.HabitDetailAdapter
import com.lawlett.habittracker.fragment.habitdetail.viewmodel.HabitDetailViewModel
import com.lawlett.habittracker.helper.*
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.models.MessageModel
import com.lawlett.habittracker.models.NotificationMessage
import com.lawlett.habittracker.models.NotificationModel
import com.lawlett.habittracker.ext.toGone
import com.lawlett.habittracker.helper.CacheManager
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail), TokenCallback {

    private val binding: FragmentHabitDetailBinding by viewBinding()
    private val adapter = HabitDetailAdapter(this::onClick)
    private val viewModel: HabitDetailViewModel by viewModels()
    lateinit var dataHelper: DataHelper
    private val timer = Timer()
    private var habitModelGlobal: HabitModel? = null
    private var isFollow = false
    private var isStartTimer = false
    private var attempts = 0

    private lateinit var timerManager: TimerManager
    var listHistory = arrayListOf<String>()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    lateinit var helper: GoogleSignInHelper

    @Inject
    lateinit var cacheManager: CacheManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        prepare()
        helper = GoogleSignInHelper(fragment = this, tokenCallback = this)
            if (!cacheManager.isPass()) {
                if (!cacheManager.isUserSeen()) {
                    searchlight()
                }
            }
            habitModelGlobal?.id?.let { id ->
                viewModel.getHistory(id)
            }
            initClickers()
            observe()
        }

    private fun observe() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.isNotificationPushed.asSharedFlow().collect() {
//                    Log.e(TAG, "onViewCreated: $it")
//                }
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tokenModelFlow.asSharedFlow().collect() {
                    val token = it.access_token
                    val notificationModel = NotificationModel(
                        MessageModel(
                            topic = "News",
                            notification = NotificationMessage("Naruto", "Uzumaki")
                        )
                    )
                    viewModel.sendRemoteNotification(notificationModel, token)
                }
            }
        }
    }

    private fun searchlight() {
        val targets = ArrayList<com.takusemba.spotlight.Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.second_target, root)

        Handler().postDelayed({
            cacheManager.saveUserSeen()
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
                binding.recordTv,
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

    private fun initAdapter() {
        binding.recyclerHistory.adapter = adapter
    }

    private fun initClickers() {
        binding.btnRelapse.setOnClickListener {
            helper.signInGoogle()

//            dialogRelapse()
        }

//        binding.appBar.setNavigationOnClickListener {
//            findNavController().navigateUp()
//        }
    }

    private fun dialogRelapse() {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtTitle.text = requireContext().getString(R.string.reset_to_zero)
        dialog.first.txtDescription.text = requireContext().getString(R.string.habit_clear)
        dialog.first.btnYes.setOnClickListener {
            launch()
            showRecord()
            timerManager.resetAction()
            timerManager.startStopAction(
                isFollow,
                habitModelGlobal?.startDate,
                habitModelGlobal?.endDate
            )
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
            habitModelGlobal?.id?.let { id ->
                viewModel.updateRecord(newRecord.toString(), id)
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
        habitModelGlobal?.let {
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
        timerManager.startStopAction(
            isFollow,
            habitModelGlobal?.startDate,
            habitModelGlobal?.endDate
        )
    }

    @SuppressLint("SetTextI18n")
    private fun prepare() {
        if (arguments != null) {
            habitModelGlobal = requireArguments().getParcelable("key") as HabitModel?
            isFollow = requireArguments().getBoolean("isFollow")
            isStartTimer = requireArguments().getBoolean("isStartTimer")
            habitModelGlobal?.let { model ->
                with(binding) {
                    iconTv.text = model.icon
                    habitProgress.max = model.allDays
                    habitTv.text = habitModelGlobal?.title
                    recordTv.text = habitModelGlobal?.record
                    viewModel.record = habitModelGlobal?.attempts ?: 0
                    habitModelGlobal?.let { model ->
                        if (model.record?.toInt() == 0 || model.record == null) {
                            recordTv.toGone()
                            recordTitleTv.toGone()
                        } else {
                            recordTv.text = habitModelGlobal?.record
                        }
                        if (model.attempts == 0) {
                            attemptCard.toGone()
                        } else {
                            tvAttempts.text =
                                "${getString(R.string.tv_attempt)} ${habitModelGlobal?.attempts.toString()}"
                        }
                        checkHistoryOnEmpty()
                    }

                    if (isFollow) {
                        dataHelper =
                            DataHelper(
                                requireActivity(),
                                "${model.title} start ${model.fbName}",
                                "${model.title} stop ${model.fbName}"
                            )
                        dataHelper.setTimerCounting(true)
                        nameTv.text = habitModelGlobal?.fbName?.replaceAfter(":", "")
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
                    habitModelGlobal?.history?.let { history ->
                        listHistory = historyToArray(history)
                        adapter.setData(listHistory)
                    }
                }
            }
        }
        timerManager = TimerManager(dataHelper, binding)

        if (dataHelper.timerCounting()) {
            timerManager.startTimer()
        }
        timer.scheduleAtFixedRate(TimeTask(), 0, 500)

        if (isStartTimer) {
            timerManager.startStopAction()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addAttempts() {
        binding.attemptCard.toVisible()
        viewModel.addAttempt()
        viewModel.attemptsNumber.observe(requireActivity()) {
            it?.let { attempts ->
                this.attempts = attempts
                binding.tvAttempts.text = getString(R.string.tv_attempt) + " " + this.attempts
                viewModel.updateAttempts(attempts, habitModelGlobal?.id!!)
            }
        }
    }

    private fun updateHistory() {
        val historyItem = viewModel.getTime()
        listHistory.add(historyItem)
        binding.historyTv.toVisible()
        adapter.setData(listHistory)
        viewModel.updateHistory(historyArrayToJson(listHistory), habitModelGlobal?.id!!)
    }

    private fun onClick(historyString: String, position: Int) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtDescription.text = "$historyString \n Будет удалена"
        dialog.first.btnYes.setOnClickListener {
            listHistory.remove(historyString)
            viewModel.updateHistory(historyArrayToJson(listHistory), habitModelGlobal?.id!!)
            viewModel.minusAttempt()
            adapter.notifyItemRemoved(position)
            checkHistoryOnEmpty()
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    private fun checkHistoryOnEmpty() {
        if (adapter.data.isEmpty()) {
            binding.historyTv.toGone()
        } else {
            binding.historyTv.toVisible()
        }
    }

    inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    timerManager.updateTime(isFollow, habitModelGlobal?.startDate)
                }
            }
        }
    }

    override fun newToken(authCode: String) {
        viewModel.getToken(authCode)
    }
}