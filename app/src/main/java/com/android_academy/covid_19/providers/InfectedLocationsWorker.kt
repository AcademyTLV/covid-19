package com.android_academy.covid_19.providers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android_academy.covid_19.db.dao.RoomUserLocationEntity
import com.android_academy.covid_19.repository.CollisionDataRepo
import com.android_academy.covid_19.repository.InfectionDataRepo
import com.android_academy.covid_19.repository.UsersLocationRepo
import com.android_academy.covid_19.repository.UsersLocationRepoImpl
import com.android_academy.covid_19.util.InfectionCollisionMatcher
import com.android_academy.covid_19.util.logTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InfectedLocationsWorker(
    appContext: Context,
    workParams: WorkerParameters
) : CoroutineWorker(appContext, workParams), KoinComponent {

    private val locationRepo: UsersLocationRepo by inject()

    private val infectionDataRepo: InfectionDataRepo by inject()

    private val collisionMatcher by inject<InfectionCollisionMatcher>()

    private val collisionDataRepo: CollisionDataRepo by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Timber.d("Starting infected location work")

            // get my last location
            locationRepo.getLocation()

            // get my cached locations for last 14 days
            Timber.d("[$logTag], doWork(): locationRepo.getUserLocationsAsync()")
            val myLocations: List<UserLocationModel> = locationRepo.getUserLocationsAsync()

            // get infected locations from server
            Timber.d("[$logTag], doWork(): infectionDataRepo.getInfectionLocationsAsync")
            val infectedLocations = infectionDataRepo.getInfectionLocationsAsync(0, 0)

            // run colliding algorithm
            Timber.d("[$logTag], doWork(): collisionMatcher.isColliding")
            val collidingUserLocations = collisionMatcher.isColliding(
                infectedLocations,
                myLocations,
                UsersLocationRepoImpl.TIME_THRESHOLD,
                UsersLocationRepoImpl.DISTANCE_THRESHOLD
            )

            // if matches show notification
            if (collidingUserLocations.isNotEmpty()) {
                Timber.d("[$logTag], doWork(): collision is not empty. calling for repo to take care")
                collisionDataRepo.onCollisionsFound(collidingUserLocations)
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "[$logTag], doWork: failed with exception")
            Result.failure()
        }
    }

    private suspend fun addDummyLocation() {
        locationRepo.saveLocation(
            RoomUserLocationEntity(
                null,
                31.8942296760001,
                34.7741629940001,
                0F,
                0F,
                1584144000000,
                "fake",
                "",
                0,
                0
            )
        )

        locationRepo.saveLocation(
            RoomUserLocationEntity(
                null,
                31.9740224,
                34.7794211,
                0F,
                0F,
                1584230400000,
                "fake",
                "",
                0,
                0
            )
        )
    }

    companion object {
        private const val TAG = "InfectedLocationsWorker"
        private const val DEFAULT_MIN_INTERVAL = 60L

        @JvmStatic
        fun schedule() {
            Timber.d("[InfectedLocationsWorker], schedule(): going to ignite download data")
            val worker = PeriodicWorkRequestBuilder<InfectedLocationsWorker>(
                DEFAULT_MIN_INTERVAL,
                TimeUnit.MINUTES
            ).addTag(TAG).build()
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, worker)
        }
    }
}
