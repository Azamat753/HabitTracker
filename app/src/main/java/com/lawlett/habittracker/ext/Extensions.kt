package com.lawlett.habittracker.ext

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lawlett.habittracker.databinding.FollowDialogBinding
import java.util.*
import kotlin.collections.ArrayList

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

fun Date.getDays(): String {
    val time = this.time - Date().time
    val days = (time / (1000 * 60 * 60 * 24))
    return days.toString().replace("-", "")
}

fun historyArrayToJson(list: ArrayList<String>): String {
    return Gson().toJson(list)
}

fun historyToArray(json: String): ArrayList<String> {
    return try {
        Gson().fromJson<ArrayList<String>>(json)
    } catch (e: Exception) {
        arrayListOf()
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

val TAG = "ololo"