package com.lawlett.habittracker.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.helper.GoogleSignInHelper
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding()
    lateinit var helper: GoogleSignInHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = GoogleSignInHelper(this)
        initClickers()
    }

    private fun initClickers() {
        with(binding) {
            signBtn.setOnClickListener {
                helper.signInGoogle()
            }
        }
    }

}