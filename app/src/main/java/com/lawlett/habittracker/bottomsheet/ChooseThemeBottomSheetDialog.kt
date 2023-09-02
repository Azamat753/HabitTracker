package com.lawlett.habittracker.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.Key.IS_SETTING
import com.lawlett.habittracker.adapter.ThemeAdapter
import com.lawlett.habittracker.databinding.ThemeLayoutBinding
import com.lawlett.habittracker.models.ThemeModel
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(IS_SETTING,true)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
    }

    private fun fillThemeModel(): ArrayList<ThemeModel> {
        val listThemeModels: ArrayList<ThemeModel> = ArrayList()
        listThemeModels.add(ThemeModel(getString(R.string.blue), "#0365C4"))
        listThemeModels.add(ThemeModel(getString(R.string.heavenly), "#73AFBA"))
        listThemeModels.add(ThemeModel(getString(R.string.green), "#6CB86F"))
        listThemeModels.add(ThemeModel(getString(R.string.pink), "#FC9885"))
        listThemeModels.add(ThemeModel(getString(R.string.black), "#323232"))
        listThemeModels.add(ThemeModel(getString(R.string.bilberry), "#464196"))
        listThemeModels.add(ThemeModel(getString(R.string.dark_brown), "#78524b"))
        listThemeModels.add(ThemeModel(getString(R.string.greyish_blue), "#3E6B8F"))
        return listThemeModels
    }
}