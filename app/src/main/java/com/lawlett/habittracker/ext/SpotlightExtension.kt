package com.lawlett.habittracker.ext

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.lawlett.habittracker.R
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.OnTargetListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.RoundedRectangle

fun setSpotLightTarget(targetView: View, backLayoutView: View, discription: String ):Target{
    val target1 = Target.Builder()
        .setAnchor(targetView)
        .setShape(RoundedRectangle(targetView.height.toFloat(), targetView.width.toFloat(), 30F))
        .setEffect(RippleEffect(100f, 200f, Color.argb(30, 124, 255, 90)))
        .setOverlay(backLayoutView)
        .setOnTargetListener(object :  OnTargetListener {
            override fun onStarted() {
                backLayoutView.findViewById<TextView>(R.id.text_target).text = discription
            }

            override fun onEnded() {
            }
        })
        .build()
    return target1
}



fun setSpotLightBuilder(activity: Activity, targets: ArrayList<com.takusemba.spotlight.Target>, backLayoutView: View){
    android.os.Handler().postDelayed({
        val spotlight = Spotlight.Builder(activity)
            .setTargets(targets)
            .setBackgroundColor(R.color.black)
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {

                }

                override fun onEnded() {

                }
            })
            .build()

        spotlight.start()

        backLayoutView.findViewById<TextView>(R.id.next).setOnClickListener { spotlight.next() }
    }, 1000)
}