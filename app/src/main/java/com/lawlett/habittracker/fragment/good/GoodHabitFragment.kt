package com.lawlett.habittracker.fragment.good

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.GoodHabitAdapter
import com.lawlett.habittracker.bottomsheet.CreateHabitDialog
import com.lawlett.habittracker.databinding.DialogCheckHabitBinding
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FragmentBadHabitBinding
import com.lawlett.habittracker.databinding.FragmentGoodHabitBinding
import com.lawlett.habittracker.ext.createDialog
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.ext.getTodayFormatDate
import com.lawlett.habittracker.ext.showToast
import com.lawlett.habittracker.ext.toGone
import com.lawlett.habittracker.ext.toVisible
import com.lawlett.habittracker.fragment.bad.viewModel.BadHabitViewModel
import com.lawlett.habittracker.fragment.good.viewModel.GoodHabitViewModel
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.Key.IS_GOOD
import com.lawlett.habittracker.models.GoodHabitModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class GoodHabitFragment : Fragment(R.layout.fragment_good_habit),
    com.lawlett.habittracker.base.BaseAdapter.IBaseAdapterClickListener<GoodHabitModel>,
    com.lawlett.habittracker.base.BaseAdapter.IBaseAdapterLongClickListenerWithModel<GoodHabitModel> {
    private val binding: FragmentGoodHabitBinding by viewBinding()
    private val viewModel: GoodHabitViewModel by viewModels()
    var adapter = GoodHabitAdapter()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    @Inject
    lateinit var cacheManager: CacheManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (getView() != null) {
            initAdapter()
            initClickers()
            viewModel.getHabits()
            observe()
            viewModel.viewModelScope.launch {
                delay(100)
                checkOnEmpty()
            }
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitFlow.asSharedFlow().collect { habits ->
                    adapter.setData(habits)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter.listener = this
        adapter.longListener = this
        binding.habitRecycler.adapter = adapter
    }

    private fun initClickers() {
        with(binding) {
            fab.setOnClickListener {
                CreateHabitDialog().show(requireActivity().supportFragmentManager, IS_GOOD)
            }
        }
    }

    private fun checkOnEmpty() {
        if (view != null) {
            with(binding) {
                if (adapter.data.isEmpty()) {
                    habitRecycler.toGone()
                    emptyLayout.toVisible()
//                    if (firebaseHelper.isSigned()) {
//                        getHabitsFromFB()
//                    }
                } else {
                    binding.progressBar.toGone()
                    habitRecycler.toVisible()
                    emptyLayout.toGone()
                }
            }
        }
    }

    override fun onClick(model: GoodHabitModel, position: Int) {
        val dialog = requireContext().createDialog(DialogCheckHabitBinding::inflate)
        dialog.first.txtDescription.text = getString(R.string.you_sure, model.title)
        dialog.first.smileTv.text = model.icon
        dialog.first.btnYes.setOnClickListener {
            checkDay(model)
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    override fun onLongClick(model: GoodHabitModel, itemView: View, position: Int) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtDescription.text = getString(R.string.habit_delete, model.title)
        dialog.first.btnYes.setOnClickListener {
            viewModel.delete(model)
            firebaseHelper.deleteGoodHabit(model)
            viewModel.viewModelScope.launch {
                delay(100)
                adapter.notifyItemRemoved(position)
            }
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkDay(habitModel: GoodHabitModel) {
        val sdf = SimpleDateFormat("dd.MM.yy hh:mm:ss")
        val currentDate = Date().getTodayFormatDate().removeRange(8, 17)
        var lastDay = "empty"

        if (habitModel.lastDate == null) {
            lastDay = "empty"
        } else {
            lastDay = sdf.format(habitModel.lastDate!!).removeRange(8, 17)
        }
        if (currentDate != lastDay) {
            val today = (habitModel.currentDay ?: 0) + 1
            viewModel.updateCurrentDay(today, Date(), habitModel.id!!)
            val fbModel = habitModel.copy(currentDay = today, lastDate = Date())
            firebaseHelper.insertOrUpdateGoodHabitFB(fbModel)
        } else {
            showToast(getString(R.string.today_check))
        }
    }
}