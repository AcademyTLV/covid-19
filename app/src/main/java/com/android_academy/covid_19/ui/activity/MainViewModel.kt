package com.android_academy.covid_19.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.providers.InfectedLocationsWorker
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.IntroFragment
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.LocationSettingsScreen
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.PermissionsBottomSheetExplanation
import com.android_academy.covid_19.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class MainNavigationTarget {
    object IntroFragment : MainNavigationTarget()
    object PermissionsBottomSheetExplanation : MainNavigationTarget()
    object LocationSettingsScreen : MainNavigationTarget()
}

interface MainViewModel {

    val navigation: LiveData<MainNavigationTarget>
    val blockingUIVisible: LiveData<Boolean>
    val error: LiveData<Throwable>

    val locationPermissionCheck: LiveData<Boolean>

    fun onLoginClick()

    fun onUserSavedType()

    fun onUserAcceptedLocationRequestExplanation()

    fun onUserPermanentlyDeniedPermission()

    fun onPermissionGranted()

    fun onGoToSettingsClick()

    fun onReturnedFromLocationSettings(hasLocationPermissions: Boolean)
}

class MainViewModelImpl(
    private val userMetaDataRepo: UserMetaDataRepo,
    private var hasLocationPermissions: Boolean
) : ViewModel(), MainViewModel {

    override val error = SingleLiveEvent<Throwable>()

    override val navigation = SingleLiveEvent<MainNavigationTarget>()

    override val locationPermissionCheck = SingleLiveEvent<Boolean>()

    override val blockingUIVisible = MutableLiveData<Boolean>()

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
                return@launch
            }

            // Verify permissions
            if (!hasLocationPermissions) {
                navigation.value = PermissionsBottomSheetExplanation
                return@launch
            }

            // Request permissions
            locationPermissionCheck.value = true
        }
    }

    private fun startWorkers() {
        InfectedLocationsWorker.schedule()
        LocationUpdateWorker.schedule()
    }

    override fun onLoginClick() {
    }

    override fun onUserSavedType() {
        initData()
    }

    override fun onUserAcceptedLocationRequestExplanation() {
        locationPermissionCheck.value = true
    }

    override fun onUserPermanentlyDeniedPermission() {
        blockingUIVisible.value = true
    }

    override fun onPermissionGranted() {
        blockingUIVisible.value = false
        startWorkers()
    }

    override fun onGoToSettingsClick() {
        navigation.value = LocationSettingsScreen
    }

    override fun onReturnedFromLocationSettings(hasLocationPermissions: Boolean) {
        this.hasLocationPermissions = hasLocationPermissions
        initData()
    }
}
