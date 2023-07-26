package com.lawlett.habittracker.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.HabitAdapter
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.bottomsheet.CreateHabitDialog
import com.lawlett.habittracker.databinding.FragmentMainBinding
import com.lawlett.habittracker.models.HabitModel

class MainFragment : Fragment(R.layout.fragment_main),BaseAdapter.IBaseAdapterClickListener<HabitModel> {
    private val binding: FragmentMainBinding by viewBinding()
    val adapter = HabitAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            CreateHabitDialog().show(requireActivity().supportFragmentManager, "")
        }
        adapter.listener= this
        binding.habitRecycler.adapter = adapter
        val list = arrayListOf<HabitModel>()
        list.add(HabitModel(title = "hey", icon = "&", allDays = "21", currentDay = 1))
        list.add(HabitModel(title = "hedsady", icon = "@", allDays = "15", currentDay = 1))
        list.add(HabitModel(title = "as", icon = "^", allDays = "24", currentDay = 15))
        list.add(HabitModel(title = "aw", icon = "!", allDays = "12", currentDay = 10))
        adapter.setData(list)
    }

    override fun onClick(model: HabitModel, position: Int) {
        findNavController().navigate(R.id.habitDetailFragment)
    }

}