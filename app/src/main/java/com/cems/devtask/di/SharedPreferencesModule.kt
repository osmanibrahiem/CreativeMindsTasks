package com.cems.devtask.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PREFERENCES_FILE_NAME: String = "com.cems.devtask.preferences.app"

val preferencesModule = module {

    single {
        providePreferences(androidContext())
    }

}

private fun providePreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )
}
