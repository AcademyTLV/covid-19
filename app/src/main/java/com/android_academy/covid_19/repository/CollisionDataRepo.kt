package com.android_academy.covid_19.repository

import com.android_academy.covid_19.db.dao.CollisionLocationsDao
import com.android_academy.covid_19.db.dao.RoomCollisionLocationEntity
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.providers.UserLocationModel
import com.android_academy.covid_19.ui.notification.CodeOrangeNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

interface CollisionDataRepo {
    fun onCollisionsFound(collidingUserLocations: List<Pair<UserLocationModel, InfectedLocationModel>>)
    fun getCollisions() : Flow<List<RoomCollisionLocationEntity>>
}

class CollisionDataRepoImpl(
    private val notificationManager: CodeOrangeNotificationManager,
    private val collisionLocationsDao: CollisionLocationsDao
) : CollisionDataRepo {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCollisionsFound(collidingUserLocations: List<Pair<UserLocationModel, InfectedLocationModel>>) {
        scope.launch {
            var isNewCollisions = false
            collidingUserLocations.forEach { collisions ->
                val collisionLocationsList = collisionLocationsDao.getCollisionLocationsList()
                collisionLocationsList.forEach { existingCollisions ->
                    isNewCollisions = addCollisionToStorage(collisions, existingCollisions)
                }
            }

            if(isNewCollisions) notificationManager.showCollisionFound()
        }
    }

    override fun getCollisions(): Flow<List<RoomCollisionLocationEntity>> = collisionLocationsDao.getCollisionLocations()

    private fun addCollisionToStorage(
        collisions: Pair<UserLocationModel, InfectedLocationModel>,
        existingCollisions: RoomCollisionLocationEntity
    ): Boolean {
        var isAdded = false
        val userLocation = collisions.first
        val infectedLocation = collisions.second
        if (isDifferent(userLocation, existingCollisions)) {
            val location = RoomCollisionLocationEntity(
                user_lat = userLocation.lat,
                user_lon = userLocation.lon,
                user_accuracy = userLocation.accuracy,
                user_speed = userLocation.speed,
                user_time = userLocation.time,
                user_provider = userLocation.provider,
                user_name = userLocation.name,
                user_timeStart = userLocation.timeStart,
                user_timeEnd = userLocation.timeEnd,
                infected_startTime = infectedLocation.startTime,
                infected_endTime = infectedLocation.startTime,
                infected_lat = infectedLocation.lat,
                infected_lon = infectedLocation.lon,
                infected_radius = infectedLocation.radius,
                infected_name = infectedLocation.name,
                comments = infectedLocation.comments
            )
            collisionLocationsDao.saveCollisionLocation(location)
            isAdded = true
        }
        return isAdded
    }

    private fun isDifferent(
        newUsersCollisions: UserLocationModel,
        existingCollisions: RoomCollisionLocationEntity
    ): Boolean {
        return newUsersCollisions.lat != existingCollisions.user_lat
            || newUsersCollisions.lon != existingCollisions.user_lon
            || newUsersCollisions.time != existingCollisions.user_time
            || newUsersCollisions.timeStart != existingCollisions.user_timeStart
            || newUsersCollisions.timeEnd != existingCollisions.user_timeEnd
    }
}