package com.android_academy.covid_19.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.providers.InfectedLocationsWorker
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.IntroFragment
import com.android_academy.covid_19.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class MainNavigationTarget {
    object IntroFragment : MainNavigationTarget()
}

interface MainViewModel {

    val navigation: LiveData<MainNavigationTarget>
    val error: LiveData<Throwable>

    val startMyLocationPeriodicJob: LiveData<Boolean>
    fun onStartedMyLocationPeriodicJob()

    fun onLoginClick()

    fun onUserSavedType()
}

class MainViewModelImpl(
    private val userMetaDataRepo: UserMetaDataRepo
) : ViewModel(), MainViewModel {

    override val error = SingleLiveEvent<Throwable>()

    override val navigation = SingleLiveEvent<MainNavigationTarget>()

    override val startMyLocationPeriodicJob = MutableLiveData<Boolean>()

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "Failed to initiate MainActivity")
            error.value = throwable
        }) {
            val currentUser = userMetaDataRepo.getCurrentUser()
            if (currentUser == null) {
                // User is not logged in
                navigation.value = IntroFragment
            }
        }
    }

    override fun onStartedMyLocationPeriodicJob() {
        startMyLocationPeriodicJob.value = null
    }

    private fun startInfectedLocationsPeriodicJob() {
        InfectedLocationsWorker.schedule()
    }

    override fun onLoginClick() {
    }

    override fun onUserSavedType() {
    }
}
