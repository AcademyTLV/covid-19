package com.android_academy.covid_19.db.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android_academy.covid_19.db.util.DBConstants.INFECTED_LOCATIONS_TABLE_NAME
import com.android_academy.covid_19.db.util.DBConstants.USERS_LOCATIONS_TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.util.Date


@Entity(tableName = INFECTED_LOCATIONS_TABLE_NAME)
data class RoomInfectedLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name ="start_time")
    val startTime: Date,
    @ColumnInfo(name ="end_time")
    val endTime: Date,
    @ColumnInfo(name ="lat")
    val lat: Double,
    @ColumnInfo(name ="lon")
    val lon: Double,
    @ColumnInfo(name ="radius")
    val radius: Double
)

@Entity(tableName = USERS_LOCATIONS_TABLE_NAME)
data class RoomLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo(name = "accuracy")
    val accuracy: Float,
    @ColumnInfo(name = "speed")
    val speed: Float,
    @ColumnInfo(name = "time")
    val time: Float
)


