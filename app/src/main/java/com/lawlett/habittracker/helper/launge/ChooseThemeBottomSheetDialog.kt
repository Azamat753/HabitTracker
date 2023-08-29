package com.lawlett.habittracker.helper.launge

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.ThemeLayoutBinding
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.Key.THEME_PREFERENCE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseThemeBottomSheetDialog :
    BaseBottomSheetDialog<ThemeLayoutBinding>(ThemeLayoutBinding::inflate),
    BaseAdapter.IBaseAdapterClickListener<ThemeModel> {

    private val adapter = ThemeAdapter()

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        adapter.listener = this
        binding.themeRecycler.adapter = adapter
        adapter.setData(fillThemeModel())
    }

    override fun onClick(model: ThemeModel, position: Int) {
        cacheManager.setTheme(position)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
    }

    private fun fillThemeModel(): ArrayList<ThemeModel> {
        val listThemeModels: ArrayList<ThemeModel> = ArrayList()
        listThemeModels.add(ThemeModel("cиний", "#0365C4"))
        listThemeModels.add(ThemeModel("оранже", "#FF5722"))
        listThemeModels.add(ThemeModel("getString(R.string.yellow)", "#FFC03D"))
        listThemeModels.add(ThemeModel("getString(R.string.heavenly)", "#73AFBA"))
        listThemeModels.add(ThemeModel("getString(R.string.red)", "#FF2525"))
        listThemeModels.add(ThemeModel("getString(R.string.green)", "#99DE9F"))
        return listThemeModels
    }
}