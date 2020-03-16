package com.android_academy.covid_19.providers

import com.android_academy.covid_19.db.dao.RoomUserLocationEntity

data class UserLocationModel(
    val id: Int? = null,
    val lat: Double,
    val lon: Double,
    val accuracy: Float,
    val speed: Float,
    val time: Long? = null,
    val timeStart: Long? = null,
    val timeEnd: Long? = null,
    val provider: String,
    val name: String? = null
)

fun fromRoomEntity(roomEntity: RoomUserLocationEntity) = UserLocationModel(
    id = roomEntity.id!!,
    lat = roomEntity.lat,
    lon = roomEntity.lon,
    accuracy = roomEntity.accuracy,
    speed = roomEntity.speed,
    time = roomEntity.time,
    provider = roomEntity.provider,
    name = roomEntity.name,
    timeStart = roomEntity.timeStart,
    timeEnd = roomEntity.timeEnd
)

fun UserLocationModel.toRoomLocationEntity() = RoomUserLocationEntity(
    lat = this.lat,
    lon = this.lon,
    accuracy = this.accuracy,
    speed = this.speed,
    time = this.time,
    provider = this.provider,
    name = this.name,
    timeStart = this.timeStart,
    timeEnd = this.timeEnd
)
