package com.android_academy.covid_19.providers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.android_academy.covid_19.util.logTag
import com.google.android.gms.location.LocationServices

interface ILocationManager {

    fun getUpdatedLocation(block: (Location?) -> Unit)
}

class LocationManager(private val appContext: Context) : ILocationManager {

    override fun getUpdatedLocation(block: (Location?) -> Unit) {

        val permission =
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(appContext).lastLocation.addOnSuccessListener { location ->
                Log.d(logTag, "Got location $location")
                block.invoke(location)
            }
        } else {
            block.invoke(null)
        }
    }
}