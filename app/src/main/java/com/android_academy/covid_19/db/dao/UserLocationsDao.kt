package com.android_academy.covid_19.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants
import com.android_academy.covid_19.providers.TimelineProviderImpl.Companion.TIMELINE_PROVIDER
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationsDao {

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM ${DBConstants.USERS_LOCATIONS_TABLE_NAME} ORDER BY time")
    fun getUserLocations(): Flow<List<RoomUserLocationEntity>>

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM ${DBConstants.USERS_LOCATIONS_TABLE_NAME} ORDER BY time")
    fun getUserLocationsAsync(): List<RoomUserLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocation(location: RoomUserLocationEntity)

    @Query("DELETE FROM ${DBConstants.USERS_LOCATIONS_TABLE_NAME} where provider = '$TIMELINE_PROVIDER'")
    fun deleteTimelineLocations()

    @Query("SELECT * FROM ${DBConstants.USERS_LOCATIONS_TABLE_NAME} where provider = '$TIMELINE_PROVIDER' ORDER BY timeEnd desc")
    fun getTimelineLocations() : List<RoomUserLocationEntity>?
}
