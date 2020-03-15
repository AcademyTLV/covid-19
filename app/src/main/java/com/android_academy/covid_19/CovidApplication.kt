package com.android_academy.covid_19

import android.app.Application
import com.android_academy.covid_19.di.modulesList
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CovidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@CovidApplication)
            modules(modulesList)
        }
    }
}
