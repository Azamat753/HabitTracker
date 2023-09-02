package com.lawlett.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ViewHolderInflater
import com.lawlett.habittracker.databinding.ItemFollowerBinding
import com.lawlett.habittracker.databinding.ItemHabitBinding
import com.lawlett.habittracker.models.HabitModel

class FollowerAdapter(var click: (model :HabitModel)-> Unit) :
    ViewHolderInflater<HabitModel, FollowerAdapter.FollowViewHolder>() {
   inner class FollowViewHolder(var binding: ItemFollowerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(habitModel: HabitModel) {
            binding.habitTitle.text = habitModel.title
            binding.habitImage.text = habitModel.icon
            binding.habitCount.text = habitModel.currentDay.toString() + " / " + habitModel.allDays
            binding.habitProgress.max = habitModel.allDays ?: 0//change
            binding.habitProgress.progress = habitModel.currentDay ?: 0
            binding.root.setOnClickListener {
                click.invoke(habitModel)
            }
        }
    }

    override fun onBindViewHolder(holder: FollowViewHolder, item: HabitModel) {
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