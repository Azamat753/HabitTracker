package com.lawlett.habittracker.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.CreateHabitDialogBinding


class CreateHabitDialog :
    BaseBottomSheetDialog<CreateHabitDialogBinding>(CreateHabitDialogBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emojiEd.addTextChangedListener(SmileyTextWatcher())
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






