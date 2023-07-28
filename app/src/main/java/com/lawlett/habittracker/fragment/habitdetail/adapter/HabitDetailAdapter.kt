package com.lawlett.habittracker.fragment.habitdetail.adapter

import android.annotation.SuppressLint
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemHistoryBinding
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.models.HistoryModel

class HabitDetailAdapter(val onClick: (HabitModel) -> Unit) : BaseAdapter<HabitModel, ItemHistoryBinding>(
    R.layout.item_history,
    listOf(),
    ItemHistoryBinding::inflate
) {
    @SuppressLint("NotifyDataSetChanged")
    override fun onBind(binding: ItemHistoryBinding, model: HabitModel) {
        binding.tvDay.text = model.date
        binding.imgClear.setOnClickListener {
            onClick(model)
            notifyDataSetChanged()
        }
    }
}