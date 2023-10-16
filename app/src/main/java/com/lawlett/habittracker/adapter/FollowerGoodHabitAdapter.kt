package com.lawlett.habittracker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ViewHolderInflater
import com.lawlett.habittracker.databinding.ItemFollowerBinding
import com.lawlett.habittracker.models.BadHabitModel
import com.lawlett.habittracker.models.GoodHabitModel

class FollowerGoodHabitAdapter(var click: (model :GoodHabitModel)-> Unit) :
    ViewHolderInflater<GoodHabitModel, FollowerGoodHabitAdapter.FollowViewHolder>() {
   inner class FollowViewHolder(var binding: ItemFollowerBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(model: GoodHabitModel) {
            binding.habitTitle.text = model.title
            binding.habitImage.text = model.icon
            binding.habitCount.text = model.currentDay.toString() + " / " + model.allDays
            binding.habitProgress.max = model.allDays ?: 0//change
            binding.habitProgress.progress = model.currentDay ?: 0
            binding.root.setOnClickListener {
                click.invoke(model)
            }
        }
    }

    override fun onBindViewHolder(holder: FollowViewHolder, item: GoodHabitModel) {
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