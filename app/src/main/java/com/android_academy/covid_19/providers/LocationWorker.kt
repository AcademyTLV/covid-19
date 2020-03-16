package com.android_academy.covid_19.providers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android_academy.covid_19.repository.IUsersLocationRepo
import com.android_academy.covid_19.util.logTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class LocationUpdateWorker(
    appContext: Context,
    workParams: WorkerParameters
) : CoroutineWorker(appContext, workParams), KoinComponent {

    private val locationRepo: IUsersLocationRepo by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(logTag, "Calling for get location")
            locationRepo.getLocation()
            Result.success()
        } catch (e: Exception) {
            Log.d(logTag, "Exception getting location -->  ${e.message}")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "UpdateLocationWorker"
        private const val DEFAULT_MIN_INTERVAL = 15L

        @JvmStatic
        fun schedule() {
            val worker = PeriodicWorkRequestBuilder<LocationUpdateWorker>(
                DEFAULT_MIN_INTERVAL,
                TimeUnit.MINUTES
            ).addTag(TAG).build()
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, worker)

            // Log.d(logTag, "Scheduling work")
            // val worker = OneTimeWorkRequestBuilder<LocationUpdateWorker>().build()
            // WorkManager.getInstance().enqueue(worker)
        }
    }
}
