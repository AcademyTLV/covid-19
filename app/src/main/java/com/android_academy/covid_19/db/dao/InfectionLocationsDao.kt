package com.android_academy.covid_19.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants.INFECTED_LOCATIONS_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface InfectionLocationsDao {

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM $INFECTED_LOCATIONS_TABLE_NAME")
    fun getAllInfectedLocations(): Flow<List<RoomInfectedLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveInfectedLocations(infectedLocations: List<RoomInfectedLocationEntity>)

    @Query("DELETE FROM $INFECTED_LOCATIONS_TABLE_NAME")
    fun deleteOldLocations()
}
