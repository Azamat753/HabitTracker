package com.lawlett.habittracker.adapter

import android.annotation.SuppressLint
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemBadHabitBinding
import com.lawlett.habittracker.databinding.ItemGoodHabitBinding
import com.lawlett.habittracker.ext.formatDateToString
import com.lawlett.habittracker.ext.getDays
import com.lawlett.habittracker.ext.getTodayFormatDate
import com.lawlett.habittracker.models.BadHabitModel
import com.lawlett.habittracker.models.GoodHabitModel
import java.text.SimpleDateFormat
import java.util.Date

class GoodHabitAdapter : BaseAdapter<GoodHabitModel, ItemGoodHabitBinding>(
    R.layout.item_good_habit,
    listOf(),
    ItemGoodHabitBinding::inflate
) {

    @SuppressLint("SetTextI18n", "StringFormatInvalid", "SimpleDateFormat")
    override fun onBind(binding: ItemGoodHabitBinding, model: GoodHabitModel) {
        val lastDay = model.lastDate?.formatDateToString()
        with(binding) {
            habitTitle.text = model.title
            habitImage.text = model.icon
            habitCount.text = model.currentDay.toString() + " / " + model.allDays
            habitProgress.max = model.allDays
            habitProgress.progress = model.currentDay ?: 0
            lastDateTv.text = if (model.lastDate==null) "Ещё не было" else lastDay
        }
    }
}