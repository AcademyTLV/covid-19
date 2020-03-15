package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.providers.ILocationManager
import com.android_academy.covid_19.providers.LocationManager
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.repository.IUsersLocationRepo
import com.android_academy.covid_19.repository.UsersLocationRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workersModule = module {

    factory<IUsersLocationRepo> {
        UsersLocationRepo(usersLocDao = get(), locationManager = get(), scope = CoroutineScope(Dispatchers.IO))
    }

    factory<ILocationManager>{
        LocationManager(androidContext())
    }

}
