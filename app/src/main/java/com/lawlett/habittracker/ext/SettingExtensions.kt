package com.lawlett.habittracker.ext

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import com.lawlett.habittracker.R
import com.lawlett.habittracker.helper.CacheManager
import java.util.Locale

fun Context.checkedTheme() {
    when (CacheManager(this).getTheme()) {
        0 -> {
            this.setTheme(R.style.AppTheme)
        }

        1 -> {
            this.setTheme(R.style.AppTheme_Heavenly)
        }

        2 -> {
            this.setTheme(R.style.AppTheme_Green)
        }

        3 -> {
            this.setTheme(R.style.AppTheme_Pink)
        }

        4 -> {
            this.setTheme(R.style.AppTheme_Dark)
        }

        5 -> {
            this.setTheme(R.style.AppTheme_Bilberry)
        }

        6 -> {
            this.setTheme(R.style.AppTheme_NewColor)
        }

        7 -> {
            this.setTheme(R.style.AppTheme_NewColor2)
        }
    }
}

fun Context.changeLanguage() {
    when (CacheManager(this).getLounge()) {
        0 -> {
            setLocale("ru", this)
        }

        1 -> {
            setLocale("en", this)
        }

        2->{
            setLocale("ky",this)
        }
    }
}
private fun setLocale(s: String, context: Context) {
    val locale = Locale(s)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    context.resources.updateConfiguration(
        config,
        context.resources.displayMetrics
    )
    CacheManager(context).saveLanguage(s)
}

fun loadLocale(context: Context) {
    val language: String? = CacheManager(context).getLanguage
    if (language != null) {
        setLocale(language, context)
    }
}