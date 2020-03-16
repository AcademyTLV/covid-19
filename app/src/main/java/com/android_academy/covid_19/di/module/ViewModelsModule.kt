@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.ui.fragment.intro.IntroViewModelImpl
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.android_academy.covid_19.ui.fragment.user_locations.UsersLocationListViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel<MainViewModelImpl> {
        MainViewModelImpl(
            userMetaDataRepo = get()
        )
    }

    viewModel<UsersLocationListViewModelImpl> {
        UsersLocationListViewModelImpl(
            usersLocRepo = get()
        )
    }

    viewModel<IntroViewModelImpl> {
        IntroViewModelImpl(userMetaDataRepo = get())
    }
}
