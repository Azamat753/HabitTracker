package com.lawlett.habittracker.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.helper.GoogleSignInHelper
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentSettingsBinding
import com.lawlett.habittracker.ext.changeLanguage
import com.lawlett.habittracker.helper.FirebaseHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding()
    lateinit var helper: GoogleSignInHelper
    @Inject lateinit var firebaseHelper: FirebaseHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.changeLang.setOnClickListener {
            requireActivity().changeLanguage()
        }
        helper = GoogleSignInHelper(this)
        initClickers()
    }

    private fun share() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, firebaseHelper.getUserName())
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }

    private fun initClickers() {
        with(binding) {
            signBtn.setOnClickListener {
                helper.signInGoogle()
            }
            shareBtn.setOnClickListener {
                share()
            }
        }
    }

}