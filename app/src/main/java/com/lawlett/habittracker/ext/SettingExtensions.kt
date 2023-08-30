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

fun Context.changeLounge() {
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

fun Activity.changeLanguage() {
    val listItems = arrayOf("Русский", "English", "Кыргызский")
    val mBuilder = AlertDialog.Builder(this)
    mBuilder.setTitle(R.string.choose_language)
    mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
        when (which) {
            0 -> {
                setLocale("ru", this)
            }

            1 -> {
                setLocale("en", this)
            }

            2 -> {
                setLocale("ky", this)
            }
        }
        this.recreate()
        dialog.dismiss()
    }
    val mDialog = mBuilder.create()
    mDialog.show()
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
    CacheManager.getInstance(context)?.saveLanguage(s)

    //TODO: this in ViewModel
}

fun loadLocale(context: Context) {
    val language: String? = LanguagePreference.getInstance(context)?.getLanguage
    if (language != null) {
        setLocale(language, context)
    }
}