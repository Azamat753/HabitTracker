package com.lawlett.habittracker

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.lawlett.habittracker.databinding.FollowDialogBinding

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun View.toGone() {
    isGone = true
}

fun View.toVisible() {
    isVisible = true
}

fun <T : ViewBinding> Context.createDialog(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> T,
): Pair<T,Dialog> {
    val inflater: LayoutInflater = LayoutInflater.from(this)
    val binding = inflate.invoke(inflater, null, false)
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return Pair(binding,dialog)
}

fun Context.agetDialog(layout: Int): Dialog {
    val inflater: LayoutInflater = LayoutInflater.from(this)
    val view: View = inflater.inflate(layout, null)
    var binding = FollowDialogBinding.inflate(inflater)
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
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