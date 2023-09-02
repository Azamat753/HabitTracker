package com.lawlett.habittracker.adapter

import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.databinding.ItemLanguageBinding

class LanguageAdapter: BaseAdapter<String, ItemLanguageBinding>(
    R.layout.item_language,
    listOf(),
    inflater = ItemLanguageBinding::inflate)
{
    override fun onBind(binding: ItemLanguageBinding, model: String) {
        binding.itemBtn.text = model
    }
}