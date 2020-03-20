package com.android_academy.covid_19.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants
import com.android_academy.covid_19.providers.TimelineProviderImpl.Companion.TIMELINE_PROVIDER
import kotlinx.coroutines.flow.Flow

@Dao
interface CollisionLocationsDao {

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM ${DBConstants.COLLISION_LOCATIONS_TABLE_NAME} ORDER BY user_time")
    fun getCollisionLocations(): Flow<List<RoomCollisionLocationEntity>>

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM ${DBConstants.COLLISION_LOCATIONS_TABLE_NAME} ORDER BY user_time")
    fun getCollisionLocationsList(): List<RoomCollisionLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCollisionLocation(location: RoomCollisionLocationEntity)
}
