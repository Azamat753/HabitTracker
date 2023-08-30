package com.lawlett.habittracker.helper.launge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lawlett.habittracker.databinding.ItemLaungeBinding

class LoungesAdapter(val onClick:(LoungeModel)->Unit,val list:ArrayList<LoungeModel>,):Adapter<LoungesAdapter.LoungesViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungesViewHolder {
        return LoungesViewHolder(ItemLaungeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount():Int = list.size

    override fun onBindViewHolder(holder: LoungesViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    inner class LoungesViewHolder(private val binding:ItemLaungeBinding):ViewHolder(binding.root){
        fun onBind(loungeModel: LoungeModel) {
            binding.signBtn.text = loungeModel.lounge
            itemView.setOnClickListener {
                onClick(loungeModel)
            }
        }
    }

}