package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.util.InfectionCollisionMatcher
import com.android_academy.covid_19.util.InfectionCollisionMatcherImpl
import org.koin.dsl.module

val utilsModule = module {

    factory<InfectionCollisionMatcher> {
        InfectionCollisionMatcherImpl()
    }
}
