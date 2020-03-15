@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.db.CodeOrangeDB
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {

    single<CodeOrangeDB> {
        CodeOrangeDB.create(androidContext())
    }

    factory<InfectionLocationsDao> {
        get<CodeOrangeDB>().infectionPointsDao()
    }
}
