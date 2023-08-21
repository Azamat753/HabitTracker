package com.lawlett.habittracker.ext

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import com.lawlett.habittracker.R
import java.util.Locale

fun Activity.changeLanguage() {
    val listItems = arrayOf("Русский","English","Кыргызский")
    val mBuilder = AlertDialog.Builder(this)
    mBuilder.setTitle(R.string.choose_language)
    mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
        when (which) {
            0 -> {
                setLocale("ru",this)
            }
            1 -> {
                setLocale("en",this)
            }
            2->{
                setLocale("ky",this)
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
        context.resources.displayMetrics)
    LanguagePreference.getInstance(context)?.saveLanguage(s)

    //TODO: this in ViewModel
}
fun loadLocale(context: Context) {
    val language: String? = LanguagePreference.getInstance(context)?.getLanguage
    if (language != null) {
        setLocale(language, context)
    }

}