package com.lawlett.habittracker.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.lawlett.habittracker.R
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.FollowDialogBinding
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.EventCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowDialog(var eventCallback: EventCallback) : BaseBottomSheetDialog<FollowDialogBinding>(FollowDialogBinding::inflate) {

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            cardTv.title.text = "Подписаться"
            applyBtn.setOnClickListener {
                val array = cacheManager.getFollowers()?:ArrayList()
                array.add(codeEd.text.toString().trim())
                FirebaseMessaging.getInstance().subscribeToTopic(codeEd.text.toString().trim())
                cacheManager.saveFollowers(array)
                eventCallback.call()
                dismiss()
            }
        }
    }
}