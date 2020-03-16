package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.providers.ILocationManager
import com.android_academy.covid_19.providers.LocationManager
import com.android_academy.covid_19.providers.TimelineProvider
import com.android_academy.covid_19.providers.TimelineProviderImpl
import com.android_academy.covid_19.repository.UsersLocationRepo
import com.android_academy.covid_19.repository.UsersLocationRepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workersModule = module {

    factory<UsersLocationRepo> {
        UsersLocationRepoImpl(
            usersLocDao = get(),
            locationManager = get(),
            scope = CoroutineScope(Dispatchers.IO)
        )
    }

    factory<ILocationManager> {
        LocationManager(androidContext())
    }

    factory<TimelineProvider> {
        TimelineProviderImpl(
            scope = CoroutineScope(Dispatchers.IO),
            usersLocationRepo = get()
        )
    }
}
