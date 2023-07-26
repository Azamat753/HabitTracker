package com.lawlett.habittracker.adapter

import android.annotation.SuppressLint
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemHabitBinding
import com.lawlett.habittracker.models.HabitModel

class HabitAdapter : BaseAdapter<HabitModel, ItemHabitBinding>(
    R.layout.item_habit,
    listOf(),
    ItemHabitBinding::inflate
) {
    @SuppressLint("SetTextI18n")
    override fun onBind(binding: ItemHabitBinding, model: HabitModel) {
        binding.habitTitle.text = model.title
        binding.habitImage.text = model.icon
        binding.habitCount.text = model.currentDay.toString() + " / " + model.allDays
        binding.habitProgress.max = model.allDays.toInt()
        binding.habitProgress.progress = model.currentDay
    }
}