package com.lawlett.habittracker.fragment.settings

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.LanguageAdapter
import com.lawlett.habittracker.bottomsheet.ChooseLanguageBottomSheetDialog
import com.lawlett.habittracker.bottomsheet.ChooseThemeBottomSheetDialog
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.DialogQrBinding
import com.lawlett.habittracker.databinding.FragmentSettingsBinding
import com.lawlett.habittracker.ext.createDialog
import com.lawlett.habittracker.ext.isClickableScreen
import com.lawlett.habittracker.ext.setSpotLightBuilder
import com.lawlett.habittracker.ext.setSpotLightTarget
import com.lawlett.habittracker.ext.showToast
import com.lawlett.habittracker.ext.toGone
import com.lawlett.habittracker.ext.toVisible
import com.lawlett.habittracker.fragment.settings.viewModel.SettingsViewModel
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.helper.GoogleSignInHelper
import com.lawlett.habittracker.helper.Key.KEY_SEARCH_SETTINGS
import com.lawlett.habittracker.helper.SpotlightEnd
import com.lawlett.habittracker.helper.TokenCallback
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), TokenCallback, SpotlightEnd {
    private val binding: FragmentSettingsBinding by viewBinding()

    lateinit var helper: GoogleSignInHelper

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    val adapter = LanguageAdapter()

    @Inject
    lateinit var cacheManager: CacheManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spotlight()
        if (getView() != null) {
            helper = GoogleSignInHelper(
                this, this
            ) { setupUI() }
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
    }

    private fun spotlight() {
        if (!cacheManager.isPass()) {
            if (!cacheManager.isUserSeen(KEY_SEARCH_SETTINGS)) {
                showSpotlight()
            }
        }
    }

    private fun showQr() {
        val dialog = requireContext().createDialog(DialogQrBinding::inflate)
        dialog.first.qrImage.setImageBitmap(getQrCodeBitmap(firebaseHelper.getUserName()))
    }

    private fun setupUI() {
        with(binding) {
            if (firebaseHelper.isSigned()) {
                signBtn.toGone()
                showQrBtn.toVisible()
                exitBtn.toVisible()
            } else {
                signBtn.toVisible()
                showQrBtn.toGone()
                exitBtn.toGone()
            }
        }
    }

    private fun showSpotlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_detail, root)
        with(binding) {
            isClickableScreen(
                false,
                signBtn, shareBtn, changeLang, changeTheme, syncBtn, exitBtn
            )
        }

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
                binding.changeTheme, first, getString(R.string.btn_change_theme)
            )

//            val syncStop = setSpotLightTarget(
//                binding.syncBtn, first, getString(R.string.btn_sync)
//            )

            targets.add(views)
            targets.add(firstStop)
            targets.add(secondStop)
            targets.add(thirdStop)
            targets.add(changeTheme)
//            targets.add(syncStop)

            setSpotLightBuilder(requireActivity(), targets, first, this)
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

    private fun getQrCodeBitmap(info: String): Bitmap {
        val size = 512 //pixels
        val qrCodeContent = info
        val hints = hashMapOf<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    private fun initClickers() {
        with(binding) {
            signBtn.setOnClickListener {
                helper.signInGoogle()
            }
            shareBtn.setOnClickListener {
                if (firebaseHelper.isSigned()) {
                    share()
                } else {
                    showToast(getString(R.string.not_sign))
                }
            }

            syncBtn.setOnClickListener {
                if (firebaseHelper.isSigned()) {
                    val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
                    dialog.first.txtTitle.text = getString(R.string.habit_sync)
                    dialog.first.txtDescription.text =
                        getString(R.string.actual_data)
                    dialog.first.btnYes.setOnClickListener {
                        viewModel.sync()
                        showProgressBar(true)
                        dialog.second.dismiss()
                    }
                    dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
                } else {
                    showToast(getString(R.string.not_sign))
                }
            }

            changeTheme.setOnClickListener {
                val bottomDialog = ChooseThemeBottomSheetDialog()
                bottomDialog.show(requireActivity().supportFragmentManager, "TAG")
            }

            changeLang.setOnClickListener {
                ChooseLanguageBottomSheetDialog().show(
                    requireActivity().supportFragmentManager,
                    "TAG"
                )
            }
            showQrBtn.setOnClickListener {
                showQr()
            }
            exitBtn.setOnClickListener {
                firebaseHelper.logOut()
                successSign()
            }
        }
    }

    override fun newToken(authCode: String) {
        successSign()
    }

    override fun signSuccess() {
        successSign()
    }

    private fun successSign() {
        showToast(getString(R.string.success))
        setupUI()
    }


    override fun end() {
        with(binding) {
            isClickableScreen(true, signBtn, shareBtn, changeLang, changeTheme, syncBtn, exitBtn)
        }
    }
}