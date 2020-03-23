package com.android_academy.covid_19.ui.activity

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.CovidApplication
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.CollisionLocationModel
import com.android_academy.covid_19.providers.InfectedLocationsWorker
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.providers.TimelineProvider
import com.android_academy.covid_19.providers.TimelineProviderImpl.Companion.TIMELINE_URL
import com.android_academy.covid_19.providers.UserLocationModel
import com.android_academy.covid_19.providers.fromRoomEntity
import com.android_academy.covid_19.repository.CollisionDataRepo
import com.android_academy.covid_19.repository.InfectionDataRepo
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.repository.UsersLocationRepo
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.IntroFragment
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.LocationSettingsScreen
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.PermissionsBottomSheetExplanation
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.StoragePermissionGranted
import com.android_academy.covid_19.ui.fragment.main.toJoda
import com.android_academy.covid_19.ui.map.MapManager
import com.android_academy.covid_19.util.SingleLiveEvent
import com.android_academy.covid_19.util.filter
import com.android_academy.covid_19.util.logTag
import com.android_academy.covid_19.util.map
import com.android_academy.covid_19.util.switchMap
import com.android_academy.covid_19.util.zipLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

sealed class MainNavigationTarget {
    object IntroFragment : MainNavigationTarget()
    object PermissionsBottomSheetExplanation : MainNavigationTarget()
    object TimelineBottomSheetExplanation : MainNavigationTarget()
    object LocationSettingsScreen : MainNavigationTarget()
    object StoragePermissionGranted : MainNavigationTarget()
    object ChangeStatusBottomSheet : MainNavigationTarget()
    object InfectionMatchGallery : MainNavigationTarget()
}

interface FilterDataModel {
    fun onChangeStatusButtonClick()
    fun onLocationMatchButtonClick()
    fun onChangeFilterDate(dateTimeStart: Date, dateTimeEnd: Date)
}

interface MainViewModel : MapManager.InteractionInterface, FilterDataModel {

    val navigation: LiveData<MainNavigationTarget>
    val myLocations: LiveData<List<LocationMarkerData>>
    val coronaLocations: LiveData<List<LocationMarkerData>>
    val collisionLocations: LiveData<List<CollisionLocationModel>>
    val blockingUIVisible: LiveData<Boolean>
    val error: LiveData<Throwable>
    val locationPermissionCheck: LiveData<Boolean>
    val toast: LiveData<String>

    fun onLoginClick()

    fun onUserSavedType()

    fun onUserAcceptedLocationRequestExplanation()

    fun onUserPermanentlyDeniedLocationPermission()

    fun onLocationPermissionGranted()

    fun onGoToLocationSettingsClick()

    fun onReturnedFromLocationSettings(hasLocationPermissions: Boolean)

    fun onScreenBecameVisible()

    fun onTimelineTriggerClicked()

    fun onUserDeniedOneOfTheLocationPermissions()

    fun onReturnedFromStorageSettings(hasStoragePermission: Boolean)
}

class MainViewModelImpl(
    private val userMetaDataRepo: UserMetaDataRepo,
    private val timelineProvider: TimelineProvider,
    private val usersLocationRepo: UsersLocationRepo,
    private val infectionDataRepo: InfectionDataRepo,
    private val collisionDataRepo: CollisionDataRepo,
    private var hasLocationPermissions: Boolean,
    private val app: Application
) : AndroidViewModel(app), MainViewModel {

    private var myLocationsLiveData: LiveData<List<LocationMarkerData>>? = null

    private var infectedMarkersLiveData: LiveData<List<LocationMarkerData>>? = null

    override val error = SingleLiveEvent<Throwable>()

    override val toast = SingleLiveEvent<String>()

    override val navigation = SingleLiveEvent<MainNavigationTarget>()

    override val myLocations = MediatorLiveData<List<LocationMarkerData>>()

    override val coronaLocations = MediatorLiveData<List<LocationMarkerData>>()

    override val collisionLocations = collisionDataRepo
        .getCollisions()
        .map { it.map { fromRoomEntity(it) } }
        .asLiveData()

    override val locationPermissionCheck = SingleLiveEvent<Boolean>()

    override val blockingUIVisible = MutableLiveData<Boolean>()

    private val datesRange = MutableLiveData<Pair<Date, Date>>()

    private val mapReady = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val prerequisites = zipLiveData(
        mapReady.filter { it == true },
        datesRange
    )

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

            startObservingMyLocations()
            startObservingCoronaLocations()

            // Verify permissions
            if (!hasLocationPermissions) {
                navigation.value = PermissionsBottomSheetExplanation
                return@launch
            }

            // Request permissions
            locationPermissionCheck.value = true
        }
    }

    private fun startObservingCoronaLocations() {
        infectedMarkersLiveData?.let {
            coronaLocations.removeSource(it)
        }

        val infectedMarkersLiveData = prerequisites
            .map { it.second }
            .switchMap { dates ->
                infectionDataRepo.getInfectionLocations()
                    .map { infectedLocations ->
                        Timber.tag("MainViewModelImpl")
                            .d("got infected locations from repo. Count: ${infectedLocations.size}")
                        val filterStartDateJoda = dates.first.toJoda()
                        val filterEndDateJoda = dates.second.toJoda()

                        return@map infectedLocations.filter { infectedLocation ->

                                val infectedStart: DateTime = infectedLocation.startTime.toJoda()
                                val infectedEnd: DateTime = infectedLocation.endTime.toJoda()

                                return@filter infectedStart.isAfter(filterStartDateJoda) &&
                                    infectedStart.isBefore(filterEndDateJoda) ||
                                    infectedEnd.isAfter(filterStartDateJoda) &&
                                    infectedEnd.isBefore(filterEndDateJoda)
                            }
                            .map { infectedLocationModel ->
                                val dateTimeInstance = SimpleDateFormat.getDateTimeInstance()
                                val dates =
                                    "${dateTimeInstance.format(infectedLocationModel.startTime)} - ${dateTimeInstance.format(
                                        infectedLocationModel.endTime
                                    )}"
                                Timber.tag("MainViewModelImpl").d("filtering : $dates")
                                LocationMarkerData(
                                    id = infectedLocationModel.id,
                                    lon = infectedLocationModel.lon,
                                    lat = infectedLocationModel.lat,
                                    title = infectedLocationModel.name ?: "",
                                    snippet = dates
                                )
                            }
                    }
                    .onEach {
                        Timber.tag("MainViewModelImpl")
                            .d("filtered infected locations from repo. Count: ${it.size}")
                    }
                    .asLiveData()
            }

        this@MainViewModelImpl.infectedMarkersLiveData = infectedMarkersLiveData
        coronaLocations.addSource(infectedMarkersLiveData) {
            coronaLocations.value = it
        }
    }

    private fun startObservingMyLocations() {
        myLocationsLiveData?.let {
            myLocations.removeSource(it)
        }

        val myLocationsLiveData = prerequisites
            .map { it.second }
            .switchMap { datesRange ->
                usersLocationRepo.getUserLocationsFlow()
                    .distinctUntilChanged()
                    .map {
                        Timber.tag("MainViewModelImpl")
                            .d("got my locations from repo. size: ${it.size}")
                        it.filter { userLocationModel ->
                                val filterStartDateJoda = datesRange.first.toJoda()
                                val filterEndDateJoda = datesRange.second.toJoda()

                                val userStartDateJoda: DateTime = userLocationModel.timeStart?.let {
                                    DateTime.now().withMillis(it)
                                } ?: DateTime.now().withMillis(userLocationModel.time!!)
                                    .minusMinutes(15)

                                val userEndDateJoda = userLocationModel.timeEnd?.let {
                                    DateTime.now().withMillis(it)
                                } ?: DateTime.now().withMillis(userLocationModel.time!!)
                                    .plusMinutes(15)

                                return@filter userStartDateJoda.isAfter(filterStartDateJoda) &&
                                    userStartDateJoda.isBefore(filterEndDateJoda) ||
                                    userEndDateJoda.isAfter(filterStartDateJoda) &&
                                    userEndDateJoda.isBefore(filterEndDateJoda)
                            }
                            .map { userLocationModel ->

                                var datesSnippet = createMyLocationDatesSnippet(userLocationModel)
                                LocationMarkerData(
                                    id = userLocationModel.id!!,
                                    lon = userLocationModel.lon,
                                    lat = userLocationModel.lat,
                                    title = userLocationModel.provider,
                                    snippet = datesSnippet
                                )
                            }
                    }.onEach {
                        Timber.tag("MainViewModelImpl")
                            .d("filtered my locations from repo. size: ${it.size}")
                    }
                    .asLiveData()
            }

        this@MainViewModelImpl.myLocationsLiveData = myLocationsLiveData
        myLocations.addSource(myLocationsLiveData) {
            myLocations.value = it
        }
    }

    private fun createMyLocationDatesSnippet(userLocationModel: UserLocationModel): String {
        val dateTimeInstance = SimpleDateFormat.getDateTimeInstance()
        var dates = dateTimeInstance.format(
            Date(userLocationModel.timeStart ?: userLocationModel.time!!)
        )
        userLocationModel.timeEnd?.let { timeEnd ->
            dates = dates.plus(" - ${dateTimeInstance.format(Date(timeEnd))}")
        }
        return dates
    }

    private fun startWorkers() {
        InfectedLocationsWorker.schedule()
        LocationUpdateWorker.schedule()
    }

    override fun onChangeFilterDate(dateTimeStart: Date, dateTimeEnd: Date) {
        Timber.d("got date time start and end $dateTimeStart - $dateTimeEnd")
        datesRange.value = Pair(dateTimeStart, dateTimeEnd)
    }

    override fun onChangeStatusButtonClick() {
        navigation.value = MainNavigationTarget.ChangeStatusBottomSheet
    }

    override fun onLocationMatchButtonClick() {
        navigation.value = MainNavigationTarget.InfectionMatchGallery
    }

    override fun onLoginClick() {
    }

    override fun onUserSavedType() {
        initData()
    }

    override fun onUserAcceptedLocationRequestExplanation() {
        locationPermissionCheck.value = true
    }

    override fun onUserPermanentlyDeniedLocationPermission() {
        blockingUIVisible.value = true
    }

    override fun onLocationPermissionGranted() {
        blockingUIVisible.value = false
        startWorkers()

        viewModelScope.launch {
            if (timelineProvider.shouldRequestData()) {
                navigation.value = MainNavigationTarget.TimelineBottomSheetExplanation
            }
        }
    }

    override fun onGoToLocationSettingsClick() {
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

    override fun onUserDeniedOneOfTheLocationPermissions() {
        hasLocationPermissions = false
        initData()
    }

    override fun onReturnedFromStorageSettings(hasStoragePermission: Boolean) {
        if (hasStoragePermission) {
            navigation.value = StoragePermissionGranted
        } else {
            toast.value =
                getApplication<CovidApplication>().getString(R.string.need_storage_permission)
        }
    }

    override fun onUserHistoryLocationMarkerSelected(data: LocationMarkerData) {
        toast.value = "User clicked ${data.title}"
    }

    override fun onMapReady() {
        mapReady.value = true
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
