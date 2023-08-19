package com.lawlett.habittracker.fragment.habitdetail.adapter

import android.annotation.SuppressLint
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemHistoryBinding

class HabitDetailAdapter(val onClick: (String) -> Unit) : BaseAdapter<String, ItemHistoryBinding>(
    R.layout.item_history,
    listOf(),
    ItemHistoryBinding::inflate
) {
    @SuppressLint("NotifyDataSetChanged")
    override fun onBind(binding: ItemHistoryBinding, date: String) {
        binding.tvDay.text=date
        binding.imgClear.setOnClickListener {
            onClick(date)
            notifyDataSetChanged()
        }
    }
}