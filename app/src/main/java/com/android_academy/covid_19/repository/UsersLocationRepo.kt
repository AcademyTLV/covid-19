package com.android_academy.covid_19.repository

import android.location.Location
import android.util.Log
import com.android_academy.covid_19.db.dao.RoomLocationEntity
import com.android_academy.covid_19.db.dao.UserLocationsDao
import com.android_academy.covid_19.db.dao.toRoomLocationEntity
import com.android_academy.covid_19.providers.ILocationManager
import com.android_academy.covid_19.util.logTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface IUsersLocationRepo {
    suspend fun getLocation()
    suspend fun saveLocation(location: RoomLocationEntity)
}

class UsersLocationRepo(
    private val usersLocDao: UserLocationsDao,
    private val locationManager: ILocationManager,
    private val scope: CoroutineScope
) : IUsersLocationRepo {
    override suspend fun getLocation() {
        locationManager.getUpdatedLocation { location ->
            Log.d(logTag, "got location $location")
            onLocationReceived(location) }
    }

    private fun onLocationReceived(location: Location?) = scope.launch {
        location?.let { newLocation ->
            saveLocation(newLocation.toRoomLocationEntity())
        }
    }

    override suspend fun saveLocation(location: RoomLocationEntity) {
        Log.d(logTag, "Saving location $location")
        usersLocDao.saveLocation(location)
    }
}