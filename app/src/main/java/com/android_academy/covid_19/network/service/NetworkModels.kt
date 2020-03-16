package com.android_academy.covid_19.network.service

import com.android_academy.covid_19.db.dao.RoomInfectedLocationEntity
import com.google.gson.annotations.SerializedName
import java.util.Date

data class InfectedLocations(@SerializedName("locations") val locations : List<InfectedLocationModel>)

data class InfectedLocationModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("startTime")
    val startTime: Date,
    @SerializedName("endTime")
    val endTime: Date,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("radius")
    val radius: Double,
    @SerializedName("name")
    val name: String?,
    @SerializedName("comments")
    val comments: String?
) {
    fun toDBModel(): RoomInfectedLocationEntity {
        return RoomInfectedLocationEntity(id, startTime, endTime, lat, lon, radius, name, comments)
    }
}
