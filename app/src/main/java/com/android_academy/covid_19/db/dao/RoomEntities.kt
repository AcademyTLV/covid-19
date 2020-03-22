package com.android_academy.covid_19.db.dao

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android_academy.covid_19.db.util.DBConstants.INFECTED_LOCATIONS_TABLE_NAME
import com.android_academy.covid_19.db.util.DBConstants.COLLISION_LOCATIONS_TABLE_NAME
import com.android_academy.covid_19.db.util.DBConstants.USERS_LOCATIONS_TABLE_NAME
import com.android_academy.covid_19.db.util.DBConstants.USERS_METADATA_TABLE_NAME
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.repository.model.UserMetaData
import com.android_academy.covid_19.repository.model.UserType
import java.util.Date

@Entity(tableName = INFECTED_LOCATIONS_TABLE_NAME)
data class RoomInfectedLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    @ColumnInfo(name = "end_time")
    val endTime: Date,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo(name = "radius")
    val radius: Double,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "comments")
    val comments: String?
) {
    fun toInfectedLocationModel(): InfectedLocationModel {
        return InfectedLocationModel(
            id, startTime, endTime, lat, lon, radius, name, comments
        )
    }
}

@Entity(tableName = USERS_LOCATIONS_TABLE_NAME)
data class RoomUserLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo(name = "accuracy")
    val accuracy: Float,
    @ColumnInfo(name = "speed")
    val speed: Float,
    @ColumnInfo(name = "time")
    val time: Long? = null,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "timeStart")
    val timeStart: Long? = null,
    @ColumnInfo(name = "timeEnd")
    val timeEnd: Long? = null
)

@Entity(tableName = COLLISION_LOCATIONS_TABLE_NAME)
data class RoomCollisionLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "user_lat")
    val user_lat: Double,
    @ColumnInfo(name = "user_lon")
    val user_lon: Double,
    @ColumnInfo(name = "user_accuracy")
    val user_accuracy: Float,
    @ColumnInfo(name = "user_speed")
    val user_speed: Float,
    @ColumnInfo(name = "user_time")
    val user_time: Long? = null,
    @ColumnInfo(name = "user_provider")
    val user_provider: String,
    @ColumnInfo(name = "user_name")
    val user_name: String? = null,
    @ColumnInfo(name = "user_timeStart")
    val user_timeStart: Long? = null,
    @ColumnInfo(name = "user_timeEnd")
    val user_timeEnd: Long? = null,
    @ColumnInfo(name = "infected_start_time")
    val infected_startTime: Date,
    @ColumnInfo(name = "infected_end_time")
    val infected_endTime: Date,
    @ColumnInfo(name = "infected_lat")
    val infected_lat: Double,
    @ColumnInfo(name = "infected_lon")
    val infected_lon: Double,
    @ColumnInfo(name = "infected_radius")
    val infected_radius: Double,
    @ColumnInfo(name = "infected_name")
    val infected_name: String?,
    @ColumnInfo(name = "infected_comments")
    val comments: String?,
    @ColumnInfo(name = "ack_by_user")
    val isAcknowledged: Boolean = false
)

@Entity(tableName = USERS_METADATA_TABLE_NAME)
data class RoomUserMetaDataEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "type")
    val type: String
) {
    fun toUserMetadata(): UserMetaData {
        return UserMetaData(
            id = id,
            type = UserType.from(type)
        )
    }
}

fun UserMetaData.toDB(): RoomUserMetaDataEntity {
    return RoomUserMetaDataEntity(
        id = id,
        type = type.strValue
    )
}

fun Location.toRoomLocationEntity() = RoomUserLocationEntity(
    lat = this.latitude,
    lon = this.longitude,
    accuracy = this.accuracy,
    speed = this.speed,
    time = this.time,
    provider = this.provider
)
