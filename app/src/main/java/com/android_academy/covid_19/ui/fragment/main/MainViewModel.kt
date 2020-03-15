package com.android_academy.covid_19.ui.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android_academy.covid_19.providers.InfectedLocationsWorker
import com.android_academy.covid_19.ui.fragment.main.MainNavigationTarget.GoogleLoginView
import com.android_academy.covid_19.util.SingleLiveEvent

sealed class MainNavigationTarget {
    object GoogleLoginView : MainNavigationTarget()
}

interface MainViewModel {

    val resultTextView: LiveData<String>
    val navigation: LiveData<MainNavigationTarget>

    val startMyLocationPeriodicJob: LiveData<Boolean>
    fun onStartedMyLocationPeriodicJob()

    fun onLoginClick()
}

class MainViewModelImpl : ViewModel(), MainViewModel {

    override val resultTextView = MutableLiveData<String>()

    override val navigation = SingleLiveEvent<MainNavigationTarget>()

    override val startMyLocationPeriodicJob = MutableLiveData<Boolean>()

    init {
        startMyLocationPeriodicJob.value = true
        startInfectedLocationsPeriodicJob()
    }

    override fun onStartedMyLocationPeriodicJob() {
        startMyLocationPeriodicJob.value = null
    }

    private fun startInfectedLocationsPeriodicJob() {
        InfectedLocationsWorker.schedule()
    }

    override fun onLoginClick() {
        navigation.value = GoogleLoginView
    }
}
