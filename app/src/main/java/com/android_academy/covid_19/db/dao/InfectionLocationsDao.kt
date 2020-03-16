package com.android_academy.covid_19.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants.INFECTED_LOCATIONS_TABLE_NAME

@Dao
interface InfectionLocationsDao {

    /* Add right query here, filter on dates*/
    @Query("SELECT * FROM $INFECTED_LOCATIONS_TABLE_NAME")
    fun getInfectedLocationsByDatesRange(): LiveData<List<RoomInfectedLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveInfectedLocations(infectedLocations: List<RoomInfectedLocationEntity>)
}
