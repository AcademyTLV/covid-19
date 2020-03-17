package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.ui.map.MapManager
import com.android_academy.covid_19.ui.map.MapManagerImpl
import com.android_academy.covid_19.util.InfectionCollisionMatcher
import com.android_academy.covid_19.util.InfectionCollisionMatcherImpl
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {

    factory<InfectionCollisionMatcher> {
        InfectionCollisionMatcherImpl()
    }

    factory<MapManager> { (interactionInterface: MapManager.InteractionInterface,
        scope: CoroutineScope) ->
        MapManagerImpl(
            interactionInterface = interactionInterface,
            context = androidContext(),
            scope = scope
        )
    }
}
