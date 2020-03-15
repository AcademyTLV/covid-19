package com.android_academy.covid_19.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android_academy.covid_19.db.util.DBConstants.INFECTED_LOCATIONS_TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = INFECTED_LOCATIONS_TABLE_NAME)
data class InfectedLocationModel(
    @PrimaryKey(autoGenerate = true)
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
