package com.lawlett.habittracker.helper.launge

import android.graphics.Color
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemThemeBinding

class ThemeAdapter : BaseAdapter<ThemeModel, ItemThemeBinding>(
    R.layout.item_theme,
    listOf(),
    inflater = ItemThemeBinding::inflate)
{
    override fun onBind(binding: ItemThemeBinding, model: ThemeModel) {
        binding.themeColorTv.text = model.colorText
        binding.themeColorTv.setBackgroundColor(Color.parseColor(model.colorAttr))
    }
}