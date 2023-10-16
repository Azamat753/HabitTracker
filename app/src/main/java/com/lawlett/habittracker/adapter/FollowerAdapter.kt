package com.lawlett.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ViewHolderInflater
import com.lawlett.habittracker.databinding.ItemFollowerBinding
import com.lawlett.habittracker.models.BadHabitModel

class FollowerAdapter(var click: (model :BadHabitModel)-> Unit) :
    ViewHolderInflater<BadHabitModel, FollowerAdapter.FollowViewHolder>() {
   inner class FollowViewHolder(var binding: ItemFollowerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(badHabitModel: BadHabitModel) {
            binding.habitTitle.text = badHabitModel.title
            binding.habitImage.text = badHabitModel.icon
            binding.habitCount.text = badHabitModel.currentDay.toString() + " / " + badHabitModel.allDays
            binding.habitProgress.max = badHabitModel.allDays ?: 0//change
            binding.habitProgress.progress = badHabitModel.currentDay ?: 0
            binding.root.setOnClickListener {
                click.invoke(badHabitModel)
            }
        }
    }

    override fun onBindViewHolder(holder: FollowViewHolder, item: BadHabitModel) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): FollowViewHolder {
        return FollowViewHolder(
            ItemFollowerBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }
}