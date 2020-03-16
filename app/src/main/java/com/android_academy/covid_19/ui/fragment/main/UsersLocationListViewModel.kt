package com.android_academy.covid_19.ui.fragment.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.providers.UserLocationModel
import com.android_academy.covid_19.repository.IUsersLocationRepo
import kotlinx.coroutines.launch

interface UsersLocationViewModel {
    fun getUserLocations(
        lifecycleOwner: LifecycleOwner,
        observer: (List<UserLocationModel>) -> Unit
    )
}

class UsersLocationListViewModelImpl(private val usersLocRepo: IUsersLocationRepo) : ViewModel(),
    UsersLocationViewModel {

    override fun getUserLocations(
        lifecycleOwner: LifecycleOwner,
        observer: (List<UserLocationModel>) -> Unit
    ) {
        viewModelScope.launch {
            usersLocRepo.getUserLocations().asLiveData().observe(lifecycleOwner, Observer {
                observer.invoke(it)
            })
        }
    }
}
