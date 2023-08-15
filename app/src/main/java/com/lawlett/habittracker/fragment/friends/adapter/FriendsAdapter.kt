package com.lawlett.habittracker.fragment.friends.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lawlett.habittracker.databinding.ItemFriendsBinding
import com.lawlett.habittracker.databinding.ItemFriendsTwoBinding

class FriendsAdapter:Adapter<ViewHolder>() {

    class ItemViewHolder(private val itemBinding:ItemFriendsBinding):ViewHolder(itemBinding.root){
        fun onBind(friendModel: FriendModel){
            itemBinding.tvName.text = friendModel.name
        }
    }

    class HeaderViewHolder(private val headerBinding:ItemFriendsTwoBinding):ViewHolder(headerBinding.root){
        fun onBind(friendModel: FriendTwoModel){
            headerBinding.tvName.text = friendModel.name
        }
    }

    private val list = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}