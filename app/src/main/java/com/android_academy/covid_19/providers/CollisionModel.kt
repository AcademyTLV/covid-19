package com.android_academy.covid_19.providers

import com.android_academy.covid_19.db.dao.RoomCollisionLocationEntity
import java.util.Date

data class CollisionLocationModel(
    val user_lat: Double,
    val user_lon: Double,
    val user_time: Long? = null,
    val user_name: String? = null,
    val user_timeStart: Long? = null,
    val user_timeEnd: Long? = null,
    val infected_startTime: Date,
    val infected_endTime: Date,
    val infected_lat: Double,
    val infected_lon: Double,
    val infected_name: String?,
    val comments: String?,
    val isAcknowledged: Boolean = false
)

fun fromRoomEntity(roomEntity: RoomCollisionLocationEntity) = CollisionLocationModel(
    user_lat = roomEntity.user_lat,
    user_lon = roomEntity.user_lon,
    user_time = roomEntity.user_time,
    user_name = roomEntity.user_name,
    user_timeStart = roomEntity.user_timeStart,
    user_timeEnd = roomEntity.user_timeEnd,
    infected_startTime = roomEntity.infected_startTime,
    infected_endTime = roomEntity.infected_endTime,
    infected_lat = roomEntity.infected_lat,
    infected_lon = roomEntity.infected_lon,
    infected_name = roomEntity.infected_name,
    comments = roomEntity.comments,
    isAcknowledged = roomEntity.isAcknowledged
)
