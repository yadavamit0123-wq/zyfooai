package com.pt.zyfooai.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {

    const val APP_LOCALE = "app_locale"

    @JvmStatic
    fun wrap(context: Context): Context {
        val lang = PreferenceManager(context).getString(APP_LOCALE)
        if (lang.isNullOrEmpty() || lang == "en") {
            return context
        }
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = Configuration(resources.configuration)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
            context
        }
    }

    @JvmStatic
    fun setLocale(context: Context, languageCode: String) {
        PreferenceManager(context).setString(APP_LOCALE, languageCode)
    }

    @JvmStatic
    fun getLocale(context: Context): String {
        val locale = PreferenceManager(context).getString(APP_LOCALE)
        return if (locale.isNullOrEmpty()) "en" else locale
    }

    @JvmStatic
    fun localeCodeFromLanguageTitle(title: String?): String {
        if (title.isNullOrBlank()) return "en"
        val lower = title.lowercase(Locale.ROOT)
        return when {
            lower.contains("hindi") || title.contains("हिंदी") -> "hi"
            lower.contains("marathi") || title.contains("मराठी") -> "mr"
            lower.contains("gujarati") || title.contains("ગુજરાત") -> "gu"
            lower.contains("tamil") || title.contains("தமிழ") -> "ta"
            lower.contains("telugu") || title.contains("తెలుగ") -> "te"
            else -> "en"
        }
    }
}
