package com.android_academy.covid_19

import android.app.Application
import com.android_academy.covid_19.di.modulesList
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CovidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initKoin()
        JodaTimeAndroid.init(this)
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@CovidApplication)
            modules(modulesList)
        }
    }
}
