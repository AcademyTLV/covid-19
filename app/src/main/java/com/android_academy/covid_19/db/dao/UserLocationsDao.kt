package com.android_academy.covid_19.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants
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
}
