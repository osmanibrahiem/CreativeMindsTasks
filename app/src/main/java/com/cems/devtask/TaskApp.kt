package com.cems.devtask

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.cems.devtask.di.mainModule
import com.cems.devtask.di.preferencesModule
import com.cems.devtask.di.retrofitModule
import com.cems.devtask.helper.LocaleManager
import com.cems.devtask.ui.service.RefreshDataService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class TaskApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleManager.setLocale(it) } ?: base)
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@TaskApp)
            modules(listOf(retrofitModule, mainModule, preferencesModule))
        }

        startService(Intent(this, RefreshDataService::class.java))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}