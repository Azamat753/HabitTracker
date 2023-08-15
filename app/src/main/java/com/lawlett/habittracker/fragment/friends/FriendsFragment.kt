package com.lawlett.habittracker.fragment.friends

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private val binding: FragmentFriendsBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}