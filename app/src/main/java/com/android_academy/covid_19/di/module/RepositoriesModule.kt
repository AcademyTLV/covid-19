@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.repository.CollisionDataRepo
import com.android_academy.covid_19.repository.CollisionDataRepoImpl
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.repository.InfectionDataRepo
import com.android_academy.covid_19.repository.InfectionDataRepoImpl
import com.android_academy.covid_19.repository.UserMetaDataRepoImpl
import com.android_academy.covid_19.ui.notification.CodeOrangeNotificationManager
import com.android_academy.covid_19.ui.notification.CodeOrangeNotificationManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val reposModule = module {

    single<InfectionDataRepo> {
        InfectionDataRepoImpl(
            dao = get(),
            service = get()
        )
    }

    single<CollisionDataRepo> {
        CollisionDataRepoImpl(
            notificationManager = get(),
            collisionLocationsDao = get()
        )
    }

    factory<UserMetaDataRepo> {
        UserMetaDataRepoImpl(usersMetaDataDao = get())
    }

    factory<CodeOrangeNotificationManager> {
        CodeOrangeNotificationManagerImpl(
            context = androidContext()
        )
    }
}
