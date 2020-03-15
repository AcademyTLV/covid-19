@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.repository.InfectionDataRepo
import com.android_academy.covid_19.repository.InfectionDataRepoImpl
import org.koin.dsl.module

val reposModule = module {

    single<InfectionDataRepo> {
        InfectionDataRepoImpl(
            dao = get(),
            service = get()
        )
    }
}
