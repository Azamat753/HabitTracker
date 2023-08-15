package com.lawlett.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ViewHolderInflater
import com.lawlett.habittracker.databinding.ItemHabitBinding
import com.lawlett.habittracker.models.HabitModel

class FollowerAdapter : ViewHolderInflater<HabitModel, FollowerAdapter.FollowViewHolder>() {
    class FollowViewHolder(var binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(habitModel: HabitModel) {
            binding.habitTitle.text = habitModel.title
            binding.habitImage.text = habitModel.icon
            binding.habitCount.text = habitModel.currentDay.toString() + " / " + habitModel.allDays
            binding.habitProgress.max = habitModel.allDays?.toInt()  ?:0//change
            binding.habitProgress.progress = habitModel.currentDay ?: 0
        }
    }

    override fun onBindViewHolder(holder: FollowViewHolder, item: HabitModel) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): FollowViewHolder {
        return FollowViewHolder(
            ItemHabitBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }
}