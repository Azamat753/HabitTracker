package com.lawlett.habittracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemHabitBinding
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.helper.DataHelper
import com.lawlett.habittracker.helper.TimerManager
import com.lawlett.habittracker.models.HabitModel
import javax.inject.Inject

class HabitAdapter : BaseAdapter<HabitModel, ItemHabitBinding>(
    R.layout.item_habit,
    listOf(),
    ItemHabitBinding::inflate
) {


    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    override fun onBind(binding: ItemHabitBinding, model: HabitModel) {
        val currentDay = model.startDate?.getDays()?.toInt() ?: 0
        val record = model.record?:"0"
        val attempts =model.attempts
        with(binding){
            habitTitle.text = model.title
            habitImage.text = model.icon
            habitCount.text = currentDay.toString() + " / " + model.allDays
            habitProgress.max = model.allDays
            habitProgress.progress = currentDay
            recordTitleTv.text = root.context?.resources?.getString(R.string.tv_record, record.toInt())
            tvAttempts.text ="${root.context?.resources?.getString(R.string.tv_attempts,attempts)}"
        }
    }
}