package com.lawlett.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ViewHolderInflater
import com.lawlett.habittracker.databinding.NameCardBinding

class NameAdapter : ViewHolderInflater<String, NameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            NameCardBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.onBind(item)
    }

    class ViewHolder(var view: NameCardBinding) : RecyclerView.ViewHolder(view.root) {
        fun onBind(name: String) {
            view.title.text = name
        }
    }
}