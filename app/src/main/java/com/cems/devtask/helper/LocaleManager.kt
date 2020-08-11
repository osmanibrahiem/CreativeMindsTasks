package com.cems.devtask.helper

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.*

object LocaleManager {

    private const val languageTag = "_language"
    const val enLanguage = "en"
    const val arLanguage = "ar"

    fun setLocale(context: Context): Context {
        return setNewLocale(context, getLanguage(context))
    }

    fun setNewLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return updateResources(context, language)
//        }
        return updateResourcesLegacy(context, language)
    }

    fun getLanguage(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var language = sharedPreferences.getString(languageTag, null)
        if (language == null) {
            language = arLanguage
            persistLanguage(context, language)
        }
        return language
    }

    private fun persistLanguage(context: Context, language: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(languageTag, language).apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.createConfigurationContext(config)
        return context
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}