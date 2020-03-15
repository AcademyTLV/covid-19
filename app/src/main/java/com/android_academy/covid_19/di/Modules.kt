@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di

import com.android_academy.covid_19.di.module.networkingModule
import com.android_academy.covid_19.di.module.viewModelsModule

val modulesList = listOf(
    networkingModule,
    viewModelsModule
)
