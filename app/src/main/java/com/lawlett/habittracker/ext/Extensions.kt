package com.lawlett.habittracker.ext

import android.app.Activity
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.databinding.FollowDialogBinding
import com.lawlett.habittracker.helper.CacheManager
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

fun String.makeTopic() = this.replaceBefore(":", "")
    .replace(":", "")


fun String.makeUserName() = this.replaceAfter(":", "")
    .replace(":", "")


fun <T : ViewBinding> Context.createDialog(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> T,
): Pair<T, Dialog> {
    val inflater: LayoutInflater = LayoutInflater.from(this)
    val binding = inflate.invoke(inflater, null, false)
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return Pair(binding, dialog)
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

fun saveNewSubscriber(name: String, cacheManager: CacheManager) {
    val array = cacheManager.getFollowers() ?: ArrayList()
    array.add(name.trim())
    FirebaseMessaging.getInstance().subscribeToTopic(name.trim().makeTopic())
    cacheManager.saveFollowers(array)
}

fun Activity.isClickableBottom(boolean: Boolean) {
    val activity: MainActivity? = this as MainActivity?
    activity?.isClickableBottom(boolean)
}

fun Fragment.isClickableScreen(isClickable: Boolean, vararg view: View) {
    view.forEach { it.isEnabled = isClickable }
    requireActivity().isClickableBottom(isClickable)
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

val TAG = "ololo"