package com.lawlett.habittracker.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.LanguageDialogBinding
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.adapter.LanguageAdapter
import com.lawlett.habittracker.helper.Key
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseLanguageBottomSheetDialog :
    BaseBottomSheetDialog<LanguageDialogBinding>(LanguageDialogBinding::inflate),
    BaseAdapter.IBaseAdapterClickListener<String> {

    private val adapter = LanguageAdapter()

    @Inject
    lateinit var cacheManager: CacheManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        adapter.listener = this
        binding.recycler.adapter = adapter
        adapter.setData(arrayListOf("Русский", "English", "Кыргызча"))
    }

    override fun onClick(model: String, position: Int) {
        cacheManager.setLanguage(position)
        val intent = Intent(requireContext(), MainActivity::class.java)
        if (tag != "main") {
            cacheManager.setChanged(true)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
        dismiss()
    }
}