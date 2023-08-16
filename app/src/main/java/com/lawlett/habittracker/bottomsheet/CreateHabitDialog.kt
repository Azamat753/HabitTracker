package com.lawlett.habittracker.bottomsheet

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.PopupMenu.OnDismissListener
import androidx.fragment.app.viewModels
import com.lawlett.habittracker.R
import com.lawlett.habittracker.TAG
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.CreateHabitDialogBinding
import com.lawlett.habittracker.fragment.MainViewModel
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ClassCastException
import javax.inject.Inject


@AndroidEntryPoint
class CreateHabitDialog(private val dismissListener: DismissListener) :
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
            nameEd.addTextChangedListener(SmileyTextWatcher())
            createBtn.setOnClickListener {
                val e = binding.emojiEd.text.toString()
                val n = binding.nameEd.text.toString()
                val errorText = "item cannot be empty"
                if (e.isEmpty() && n.isEmpty()) {
                    nameEd.error = errorText
                    emojiEd.error = errorText
                    nameEd.setBackgroundResource(R.drawable.edit_error_bg)
                    emojiEd.setBackgroundResource(R.drawable.edit_error_bg)
                } else if(e.isEmpty()) {
                    emojiEd.error = errorText
                    emojiEd.setBackgroundResource(R.drawable.edit_error_bg)
                } else if(n.isEmpty()) {
                    nameEd.error = errorText
                    nameEd.setBackgroundResource(R.drawable.edit_error_bg)
                }
                else {
                    val model = HabitModel(
                        title = nameEd.text.toString(),
                        icon = emojiEd.text.toString().ifEmpty { "$" }, allDays = "7"
                    )
                    showToast(model.title.toString())
                    viewModel.insert(model)
                    dismissListener.onDismiss()
                    firebaseHelper.insertOrUpdateHabitFB(model)
                    dismiss()
                }
            }
        }
    }

    inner class SmileyTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.emojiEd.text.isNotEmpty()) {
                binding.emojiEd.setBackgroundResource(R.drawable.edit_bg)
            } else if (binding.nameEd.text.isNotEmpty()) {
                binding.nameEd.setBackgroundResource(R.drawable.edit_bg)
            }
        }
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val text = it.toString().trim()
                if (text.isNotEmpty()) {
                    val regex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+".toRegex()
                    if (!text.matches(regex)) {
                        val errorText = "Только один смайлик"
                        binding.emojiEd.text?.clear()
                        binding.emojiEd.error = errorText
                        binding.emojiEd.setBackgroundResource(R.drawable.edit_error_bg)
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

    interface DismissListener {
        fun onDismiss()
    }

}






