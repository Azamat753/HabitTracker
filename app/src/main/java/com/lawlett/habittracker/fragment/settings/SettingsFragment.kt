package com.lawlett.habittracker.fragment.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.helper.GoogleSignInHelper
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.FragmentSettingsBinding
import com.lawlett.habittracker.ext.changeLanguage
import com.lawlett.habittracker.ext.setSpotLightBuilder
import com.lawlett.habittracker.ext.setSpotLightTarget
import com.lawlett.habittracker.fragment.settings.viewModel.SettingsViewModel
import com.lawlett.habittracker.helper.FirebaseHelper
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding()
    lateinit var helper: GoogleSignInHelper
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // if (!viewModel.isUserSeen()) {
            searchlight()
       // }
        binding.changeLang.setOnClickListener {
            requireActivity().changeLanguage()
        }
        helper = GoogleSignInHelper(this)
        initClickers()
    }

    private fun searchlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.second_target, root)
        val view = View(requireContext())

        Handler().postDelayed({
            //viewModel.saveUserSeen()
            val views = setSpotLightTarget(
                binding.mainSettings, first, getString(R.string.settings_display)
            )

            val firstStop = setSpotLightTarget(
                binding.signBtn, first, getString(R.string.settings_display_singBtn)
            )

            val secondStop = setSpotLightTarget(
                binding.shareBtn, first, getString(R.string.settings_display_shareBtn)
            )
            val thirdStop = setSpotLightTarget(
                binding.changeLang, first, getString(R.string.settings_display_changeLang)
            )

            targets.add(views)
            targets.add(firstStop)
            targets.add(secondStop)
            targets.add(thirdStop)
            setSpotLightBuilder(requireActivity(), targets, first)
        }, 100)
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