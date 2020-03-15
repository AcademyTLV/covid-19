package com.android_academy.covid_19.network.service

import com.google.gson.annotations.SerializedName
import java.util.Date

data class InfectedLocationModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("start_time")
    val startTime: Date,
    @SerializedName("end_time")
    val endTime: Date,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("radius")
    val radius: Double
)