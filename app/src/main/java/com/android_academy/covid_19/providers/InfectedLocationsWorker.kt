package com.android_academy.covid_19.providers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android_academy.covid_19.repository.IUsersLocationRepo
import com.android_academy.covid_19.repository.InfectionDataRepo
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

    private val locationRepo: IUsersLocationRepo by inject()

    private val infectionDataRepo: InfectionDataRepo by inject()

    // private val notificationManager: NotificationManagerCovid by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Timber.d("Starting infected location work")
            // get my last location
            locationRepo.getLocation()

            // get my cached locations for last 14 days
            // locationRepo.getMyCachedLocations()

            // get infected locations from server
            val infectedLocations = infectionDataRepo.getInfectionLocationsAsync(0, 0)

            // run colliding algorithm

            // if matches show notification

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "InfectedLocationsWorker"
        private const val DEFAULT_MIN_INTERVAL = 60L

        @JvmStatic
        fun schedule() {
            val worker = PeriodicWorkRequestBuilder<InfectedLocationsWorker>(
                DEFAULT_MIN_INTERVAL,
                TimeUnit.MINUTES
            ).addTag(TAG).build()
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, worker)
        }
    }
}
