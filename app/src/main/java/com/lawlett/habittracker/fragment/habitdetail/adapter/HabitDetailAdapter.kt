package com.lawlett.habittracker.fragment.habitdetail.adapter

import android.annotation.SuppressLint
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemHistoryBinding
import com.lawlett.habittracker.ext.toGone

class HabitDetailAdapter(val onClick: (String, Int) -> Unit,val isFollow: Boolean = false) :
    BaseAdapter<String, ItemHistoryBinding>(
        R.layout.item_history,
        listOf(),
        ItemHistoryBinding::inflate
    ) {
    @SuppressLint("NotifyDataSetChanged")
    override fun onBind(binding: ItemHistoryBinding, date: String) {
        if (isFollow){
            binding.imgClear.toGone()
        }
        binding.tvDay.text = date
        binding.imgClear.setOnClickListener {
            onClick(date, positionAdapter - 1)
            notifyDataSetChanged()
        }
    }
}