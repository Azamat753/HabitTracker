package com.lawlett.habittracker

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun View.toGone() {
    isGone = true
}

fun Context.getDialog(layout: Int): Dialog {
    val inflater: LayoutInflater = LayoutInflater.from(this)
    val view: View = inflater.inflate(layout, null)
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

val TAG = "ololo"