package com.lawlett.habittracker.fragment.habitdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import com.lawlett.habittracker.helper.Key.KEY_SEARCH_DETAIL
import com.lawlett.habittracker.models.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail), TokenCallback, SpotlightEnd {

    private val binding: FragmentHabitDetailBinding by viewBinding()
    private var adapter = HabitDetailAdapter(this::onClick)
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
        getDataArguments()
        initAdapter()
        prepare()
        initClickers()
        observe()
    }

    private fun getDataArguments() {
        if (arguments != null) {
            isFollow = requireArguments().getBoolean("isFollow")
            habitModelGlobal = requireArguments().getParcelable("key") as HabitModel?
            isFollow = requireArguments().getBoolean("isFollow")
            isStartTimer = requireArguments().getBoolean("isStartTimer")
        }

    }


    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tokenModelFlow.asSharedFlow().collect() {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(it: TokenModel) {
        val name = firebaseHelper.getUserName().makeUserName()
        val topic = firebaseHelper.getUserName().makeTopic()
        val token = it.access_token
        val notificationModel = NotificationModel(
            MessageModel(
                topic = topic,
                notification = NotificationMessage(
                    name,
                    getString(R.string.relapse_habit, habitModelGlobal?.title.toString())
                )
            )
        )
        viewModel.sendRemoteNotification(notificationModel, token)
    }

    private fun searchlight() {
        val targets = ArrayList<com.takusemba.spotlight.Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_detail, root)
        isClickableScreen(false, binding.btnRelapse,binding.backArrowImg)
        Handler().postDelayed({
            cacheManager.saveUserSeen(KEY_SEARCH_DETAIL)
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

            targets.add(views)
            targets.add(zeroStop)
            targets.add(firstSpot)
            targets.add(secondSpot)
            targets.add(thirdSpot)
            setSpotLightBuilder(requireActivity(), targets, first,this)
        }, 100)
    }

    private fun initAdapter() {
        adapter = HabitDetailAdapter(this::onClick, isFollow)
        binding.recyclerHistory.adapter = adapter
    }

    private fun initClickers() {
        binding.btnRelapse.setOnClickListener {
            dialogRelapse()
        }
        binding.backArrowImg.setOnClickListener {
            findNavController().navigateUp()
        }
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
            if (firebaseHelper.isSigned()) {
                helper.signInGoogle()
            }
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }


    @SuppressLint("SetTextI18n")
    private fun showRecord() {
        val nowRecord =
            binding.recordTitleTv.text.toString().substringAfter(":").trim().ifEmpty { "0" }.toInt()
        val newRecord = dataHelper.startTimeFromPref()?.getDays().toString().toInt()
        if (newRecord > nowRecord) {
            binding.recordTitleTv.toVisible()
            binding.recordTitleTv.toVisible()
            binding.recordTitleTv.text = getString(R.string.tv_record, newRecord.toString().toInt())
            habitModelGlobal?.id?.let { id ->
                viewModel.updateRecord(newRecord.toString(), id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun launch() {
        timerManager = TimerManager(dataHelper, binding)
        changeAttempts()
        updateHistory()
        habitModelGlobal?.let {
            val model = HabitModel(
                id = it.id,
                title = it.title,
                allDays = it.currentDay + 7,
                currentDay = it.currentDay,
                icon = it.icon,
                startDate = Date(),
                endDate = dataHelper.stopTime(),
                history = historyArrayToJson(listHistory),
                attempts = it.attempts,
                record = it.record
            )
            firebaseHelper.insertOrUpdateHabitFB(model)
            viewModel.update(model)
        }
        timerManager.startStopAction(
            isFollow,
            habitModelGlobal?.startDate,
            habitModelGlobal?.endDate
        )
    }

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    private fun prepare() {
        val record = habitModelGlobal?.record ?: 0
        habitModelGlobal?.let { model ->
            with(binding) {
                iconTv.text = model.icon
                habitProgress.max = model.allDays
                habitTv.text = habitModelGlobal?.title
                recordTitleTv.text = getString(R.string.tv_record, record.toString().toInt())
                viewModel.record = habitModelGlobal?.attempts ?: 0
                habitModelGlobal?.let { model ->
                    if (model.record?.toInt() == 0 || model.record == null) {
                        recordTitleTv.toGone()
                    } else {
                        recordTitleTv.text =
                            getString(R.string.tv_record, record.toString().toInt())
                    }
                    if (model.attempts == 0) {
                        attemptCard.toGone()
                    } else {
                        viewModel.attemptsNumbers.value = habitModelGlobal?.attempts ?: 0
                        val attempts = viewModel.attemptsNumbers.value
                        tvAttempts.text =
                            getString(R.string.tv_attempts, attempts)
                    }
                }

                if (isFollow) {
                    dataHelper =
                        DataHelper(
                            requireActivity(),
                            "${model.title} start ${model.fbName}",
                            "${model.title} stop ${model.fbName}"
                        )
                    dataHelper.setTimerCounting(true)
                    nameTv.text = habitModelGlobal?.fbName?.makeUserName()
                    btnRelapse.toGone()
                } else {
                    nameTv.toGone()
                    dataHelper =
                        DataHelper(
                            requireActivity(),
                            "${model.title} start",
                            "${model.title} stop"
                        )
                    if (!habitModelGlobal?.fbName.isNullOrEmpty() && !isStartTimer) {
                        //fixme
                        dataHelper.setStartTime(habitModelGlobal?.startDate)
                        dataHelper.setTimerCounting(true)
                    }
                }
                habitProgress.progress = dataHelper.startTimeFromPref()?.getDays()?.toInt() ?: 0
                habitModelGlobal?.history?.let { history ->
                    listHistory = historyToArray(history)
                    adapter.setData(listHistory)
                    checkHistoryOnEmpty()
                }.run {
                    checkHistoryOnEmpty()
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
        helper = GoogleSignInHelper(fragment = this, tokenCallback = this)
        if (!cacheManager.isPass()) {
            if (!cacheManager.isUserSeen(KEY_SEARCH_DETAIL)) {
                searchlight()
            }
        }
        habitModelGlobal?.id?.let { id ->
            viewModel.getHistory(id)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeAttempts(isMinus: Boolean = false) {
        binding.attemptCard.toVisible()
        if (isMinus) {
            viewModel.minusAttempt()
        } else {
            viewModel.addAttempt()
        }
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

    @SuppressLint("SetTextI18n")
    private fun onClick(historyString: String, position: Int) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtDescription.text = "$historyString \n ${getString(R.string.tv_delete)}"
        dialog.first.btnYes.setOnClickListener {
            listHistory.remove(historyString)
            changeAttempts(true)
            viewModel.updateHistory(historyArrayToJson(listHistory), habitModelGlobal?.id!!)
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

    override fun signSuccess() {}
    override fun end() {
        isClickableScreen(true, binding.btnRelapse,binding.backArrowImg)
    }
}