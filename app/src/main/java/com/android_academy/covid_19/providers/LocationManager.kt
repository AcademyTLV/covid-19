package com.android_academy.covid_19.providers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.android_academy.covid_19.util.logTag
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ILocationManager {

    suspend fun getUpdatedLocation(): Location?
}

class LocationManager(private val appContext: Context) : ILocationManager {

    override suspend fun getUpdatedLocation() : Location? {

        return suspendCoroutine {
            val permission =
                ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                LocationServices.getFusedLocationProviderClient(appContext).lastLocation.addOnSuccessListener { location ->
                    Log.d(logTag, "Got location $location")
                    it.resume(location)
                }
            } else {
                it.resume(null)
            }
        }
    }
}