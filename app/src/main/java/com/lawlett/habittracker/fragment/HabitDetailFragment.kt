package com.lawlett.habittracker.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentHabitDetailBinding

class HabitDetailFragment : Fragment(R.layout.fragment_habit_detail) {
    private val binding: FragmentHabitDetailBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener {
        }


    }
}