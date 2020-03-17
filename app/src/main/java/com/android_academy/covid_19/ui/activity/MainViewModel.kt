package com.android_academy.covid_19.ui.activity

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.providers.InfectedLocationsWorker
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.providers.TimelineProvider
import com.android_academy.covid_19.providers.TimelineProviderImpl.Companion.TIMELINE_URL
import com.android_academy.covid_19.repository.InfectionDataRepo
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.repository.UsersLocationRepo
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.IntroFragment
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.LocationSettingsScreen
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.PermissionsBottomSheetExplanation
import com.android_academy.covid_19.ui.map.MapManager
import com.android_academy.covid_19.util.SingleLiveEvent
import com.android_academy.covid_19.util.logTag
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar

sealed class MainNavigationTarget {
    object IntroFragment : MainNavigationTarget()
    object PermissionsBottomSheetExplanation : MainNavigationTarget()
    object LocationSettingsScreen : MainNavigationTarget()
}

interface MainViewModel : MapManager.InteractionInterface {

    val navigation: LiveData<MainNavigationTarget>
    val myLocations: LiveData<List<LocationMarkerData>>
    val coronaLocations: LiveData<List<LocationMarkerData>>
    val blockingUIVisible: LiveData<Boolean>
    val error: LiveData<Throwable>
    val locationPermissionCheck: LiveData<Boolean>
    val toast: LiveData<String>

    fun onLoginClick()

    fun onUserSavedType()

    fun onUserAcceptedLocationRequestExplanation()

    fun onUserPermanentlyDeniedPermission()

    fun onPermissionGranted()

    fun onGoToSettingsClick()

    fun onReturnedFromLocationSettings(hasLocationPermissions: Boolean)

    fun onScreenBecameVisible()

    fun onTimelineTriggerClicked()
}

class MainViewModelImpl(
    private val userMetaDataRepo: UserMetaDataRepo,
    private val timelineProvider: TimelineProvider,
    private val usersLocationRepo: UsersLocationRepo,
    private val infectionDataRepo: InfectionDataRepo,
    private var hasLocationPermissions: Boolean,
    private val app: Application
) : AndroidViewModel(app), MainViewModel {

    private var myLocationsJob: Job? = null

    private var coronaJob: Job? = null

    override val error = SingleLiveEvent<Throwable>()

    override val toast = SingleLiveEvent<String>()

    override val navigation = SingleLiveEvent<MainNavigationTarget>()

    override val myLocations = MutableLiveData<List<LocationMarkerData>>()

    override val coronaLocations = MutableLiveData<List<LocationMarkerData>>()

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

            startObservingMyLocations()
            startObservingCoronaLocations()
        }
    }

    private fun startObservingCoronaLocations() {
        if (coronaJob?.isActive == true) coronaJob?.cancel()
        coronaJob = viewModelScope.launch {
            infectionDataRepo.getInfectionLocations()
                .collect {
                    val markerDatas = it.map { infectedLocationModel ->
                        LocationMarkerData(
                            id = infectedLocationModel.id,
                            lon = infectedLocationModel.lon,
                            lat = infectedLocationModel.lat,
                            title = infectedLocationModel.name ?: ""
                        )
                    }
                    coronaLocations.value = markerDatas
                }
        }
    }

    private fun startObservingMyLocations() {
        if (myLocationsJob?.isActive == true) myLocationsJob?.cancel()
        myLocationsJob = viewModelScope.launch {
            usersLocationRepo.getUserLocations()
                .collect {
                    val markerDatas = it.map { userLocationModel ->
                        LocationMarkerData(
                            id = userLocationModel.id!!,
                            lon = userLocationModel.lon,
                            lat = userLocationModel.lat,
                            title = userLocationModel.provider
                        )
                    }
                    myLocations.value = markerDatas
                }
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

    override fun onScreenBecameVisible() {
        timelineProvider.checkForExistingKMLFiles()
    }

    override fun onTimelineTriggerClicked() {
        fireTimelineDownloadEvents()
    }

    override fun onUserHistoryLocationMarkerSelected(data: LocationMarkerData) {
        toast.value = "User clicked ${data.title}"
    }

    private fun fireTimelineDownloadEvents() {
        val instance = Calendar.getInstance()
        for (i in 1..14) {
            val dayStart = instance.get(Calendar.DAY_OF_MONTH)
            val monthStart = instance.get(Calendar.MONTH)
            val yearStart = instance.get(Calendar.YEAR)
            instance.add(Calendar.DAY_OF_MONTH, -1)
            val dayEnd = instance.get(Calendar.DAY_OF_MONTH)
            val monthEnd = instance.get(Calendar.MONTH)
            val yearEnd = instance.get(Calendar.YEAR)
            Timber.d("[$logTag], fireTimelineDownloadEvents():fire intent for dates from $yearEnd-$monthEnd-$dayEnd to $yearStart-$monthStart-$dayStart ")
            val url = String.format(
                TIMELINE_URL,
                yearEnd,
                monthEnd,
                dayEnd,
                yearStart,
                monthStart,
                dayStart
            )
            // "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i$yearEnd!2i$monthEnd!3i$dayEnd!2m3!1i$yearStart!2i$monthStart!3i$dayStart"
            startChromeFileDownload(url)
        }
    }

    private fun startChromeFileDownload(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            app.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null)
            app.startActivity(intent)
        }
    }
}
