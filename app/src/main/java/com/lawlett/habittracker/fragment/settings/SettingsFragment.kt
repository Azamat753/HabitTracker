package com.lawlett.habittracker.fragment.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FragmentSettingsBinding
import com.lawlett.habittracker.ext.*
import com.lawlett.habittracker.fragment.settings.viewModel.SettingsViewModel
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.GoogleSignInHelper
import com.lawlett.habittracker.helper.Key.KEY_SEARCH_SETTINGS
import com.lawlett.habittracker.helper.launge.ChooseLoungeBottomSheetDialog
import com.lawlett.habittracker.helper.theme.ChooseThemeBottomSheetDialog
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding()

    lateinit var helper: GoogleSignInHelper

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    @Inject
    lateinit var cacheManager: CacheManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!cacheManager.isPass()) {
            if (!cacheManager.isUserSeen(KEY_SEARCH_SETTINGS)) {
                searchlight()
            }
        }
        helper = GoogleSignInHelper(this)
        initClickers()
        setupUI()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.completeFlow.asSharedFlow().collect() {
                    showProgressBar(it)
                }
            }
        }
    }

    private fun setupUI() {
        with(binding) {
            if (firebaseHelper.isSigned()) {
                signBtn.toGone()
            } else {
                signBtn.toVisible()
            }
        }
    }

    private fun searchlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_detail, root)

        Handler().postDelayed({
            cacheManager.saveUserSeen(KEY_SEARCH_SETTINGS)
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

            val changeTheme = setSpotLightTarget(
                binding.changeTheme,first, getString(R.string.btn_change_theme)
            )

            val syncStop = setSpotLightTarget(
                binding.syncBtn,first, getString(R.string.btn_sync)
            )

            targets.add(views)
            targets.add(firstStop)
            targets.add(secondStop)
            targets.add(thirdStop)
            targets.add(changeTheme)
            targets.add(syncStop)

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

    private fun showProgressBar(visible: Boolean) {
        if (visible) {
            binding.progressBar.toVisible()
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.progressBar.toGone()
        }
    }

    private fun initClickers() {
        with(binding) {
            signBtn.setOnClickListener {
                helper.signInGoogle()
            }
            shareBtn.setOnClickListener {
                share()
            }
            syncBtn.setOnClickListener {
                val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
                dialog.first.txtTitle.text = "Привычки будут синхронизированы"
                dialog.first.txtDescription.text =
                    "Актуализация данных для подписчиков"
                dialog.first.btnYes.setOnClickListener {
                    viewModel.sync()
                    showProgressBar(true)
                    dialog.second.dismiss()
                }
                dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
            }

            binding.changeTheme.setOnClickListener {
                val bottomDialog = ChooseThemeBottomSheetDialog()
                bottomDialog.show(requireActivity().supportFragmentManager, "TAG")
            }

            binding.changeLang.setOnClickListener {
                val bottomDialog = ChooseLoungeBottomSheetDialog()
                bottomDialog.show(requireActivity().supportFragmentManager, "TAG")

            }
        }
    }
//                changeLounge()
//                requireActivity().changeLanguage()
}