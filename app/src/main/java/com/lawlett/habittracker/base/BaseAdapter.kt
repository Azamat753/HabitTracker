package com.lawlett.habittracker.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lawlett.habittracker.R

abstract class BaseAdapter<T, Binding : ViewBinding>(
    private val holderLayoutId: Int,
    var data: List<T>,
    private val inflater: (LayoutInflater,ViewGroup,Boolean) -> Binding
) : RecyclerView.Adapter<BaseAdapter<T, Binding>.BaseViewHolder>() {

    var listener: IBaseAdapterClickListener<T>? = null
    var longListener: IBaseAdapterLongClickListenerWithModel<T>? = null
    private var _binding: Binding? = null
    val binding get() = _binding!!
    var lastPosition: Int = -1
    var positionAdapter: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        _binding = inflater.invoke(LayoutInflater.from(parent.context),parent,false)
        return BaseViewHolder(binding)
    }

    @JvmName("setData1")
    fun setData(data: List<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun remove(model: T) {
        (this.data as ArrayList<T>).remove(model)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])
        setAnimation(holder.itemView, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    abstract fun onBind(binding: Binding, model: T)

    private fun setAnimation(viewToAnimation: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(viewToAnimation.context, R.anim.item_anim)
            viewToAnimation.startAnimation(animation)
            lastPosition = position
        }
    }

    @JvmName("getData1")
    fun getData(): List<T> {
        return data
    }

    inner class BaseViewHolder(binding: Binding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: T) {
            onBind(binding, model)
            positionAdapter= adapterPosition
            itemView.setOnClickListener { listener?.onClick(model, adapterPosition) }

            itemView.setOnLongClickListener {
                longListener?.onLongClick(model, itemView, adapterPosition)
                return@setOnLongClickListener true
            }
        }
    }

    interface IBaseAdapterClickListener<T> {
        fun onClick(model: T, position: Int)
    }

    interface IBaseAdapterLongClickListenerWithModel<T> {
        fun onLongClick(model: T, itemView: View, position: Int)
    }
}