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
    fun getCollisions(): Flow<List<RoomCollisionLocationEntity>>
    fun deleteAll()
}

class CollisionDataRepoImpl(
    private val notificationManager: CodeOrangeNotificationManager,
    private val collisionLocationsDao: CollisionLocationsDao
) : CollisionDataRepo {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCollisionsFound(foundCollidingUserLocations: List<Pair<UserLocationModel, InfectedLocationModel>>) {
        scope.launch {
            var isNewCollisions = false
            val existingCollisionList = collisionLocationsDao.getCollisionLocationsList()

            foundCollidingUserLocations.forEach { collisions ->
                isNewCollisions = addCollisionToStorage(collisions, existingCollisionList)
            }

            if (isNewCollisions) notificationManager.showCollisionFound()
        }
    }

    override fun getCollisions(): Flow<List<RoomCollisionLocationEntity>> =
        collisionLocationsDao.getCollisionLocations()

    override fun deleteAll() {
        collisionLocationsDao.deleteAll()
    }

    private fun addCollisionToStorage(
        collisions: Pair<UserLocationModel, InfectedLocationModel>,
        existingCollisions: List<RoomCollisionLocationEntity>
    ): Boolean {
        val userLocation = collisions.first
        val infectedLocation = collisions.second
        var isAdded = false
        if (existingCollisions.isEmpty()) {
            saveCollision(userLocation, infectedLocation)
            isAdded = true
        } else if (isDifferent(userLocation, existingCollisions)) {
            saveCollision(userLocation, infectedLocation)
            isAdded = true
        }
        return isAdded
    }

    private fun saveCollision(
        userLocation: UserLocationModel,
        infectedLocation: InfectedLocationModel
    ) {
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
    }

    private fun isDifferent(
        newUsersCollisions: UserLocationModel,
        existingCollisions: List<RoomCollisionLocationEntity>
    ): Boolean {
        return existingCollisions.any { existingCollision ->
            newUsersCollisions.lat == existingCollision.user_lat
                && newUsersCollisions.lon == existingCollision.user_lon
                && newUsersCollisions.time == existingCollision.user_time
                && newUsersCollisions.timeStart == existingCollision.user_timeStart
                && newUsersCollisions.timeEnd == existingCollision.user_timeEnd
        }
    }
}