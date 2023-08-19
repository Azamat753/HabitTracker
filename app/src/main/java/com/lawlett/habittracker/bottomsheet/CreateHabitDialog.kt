package com.lawlett.habittracker.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.CreateHabitDialogBinding
import com.lawlett.habittracker.fragment.MainViewModel
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateHabitDialog :
    BaseBottomSheetDialog<CreateHabitDialogBinding>(CreateHabitDialogBinding::inflate) {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickers()
    }

    private fun initClickers() {
        with(binding) {
            emojiEd.addTextChangedListener(SmileyTextWatcher())
            createBtn.setOnClickListener {
                val model = HabitModel(
                    title = nameEd.text.toString(),
                    icon = emojiEd.text.toString().ifEmpty { "$" }, allDays = "7",
                    fbName = firebaseHelper.getUserName(),
                    history = arrayListOf()
                )
                viewModel.insert(model)
                firebaseHelper.insertOrUpdateHabitFB(model)
                dismiss()
                val bundle = Bundle()
                bundle.putParcelable("key",model)
                bundle.putBoolean("isStartTimer",true)
                findNavController().navigate(R.id.habitDetailFragment,bundle)
            }
        }
    }


    inner class SmileyTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val text = it.toString().trim()
                if (text.isNotEmpty()) {
                    val regex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+".toRegex()
                    if (!text.matches(regex)) {
                        val errorText = "Только один смайлик"
                        binding.emojiEd.text?.clear()
                        binding.emojiEd.error = errorText
                    } else {
                        binding.emojiEd.error = null
                        val matchResult = regex.find(text)
                        if (matchResult != null) {
                            val firstSmileyIndex = matchResult.range.first
                            val lastSmileyIndex = matchResult.range.last
                            if (firstSmileyIndex != lastSmileyIndex) {
                                s.delete(firstSmileyIndex + 1, lastSmileyIndex)
                            }
                        }
                    }
                }
            }
        }
    }
}






