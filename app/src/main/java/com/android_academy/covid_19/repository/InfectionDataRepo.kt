package com.android_academy.covid_19.repository

import androidx.lifecycle.LiveData
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.db.dao.RoomInfectedLocationEntity
import com.android_academy.covid_19.network.service.InfectionDataService

interface InfectionDataRepo {
    fun getInfectionLocations(startDate: Long, endDate: Long): LiveData<RoomInfectedLocationEntity>
}

class InfectionDataRepoImpl(
    private val dao: InfectionLocationsDao,
    private val service: InfectionDataService
) : InfectionDataRepo {

    override fun getInfectionLocations(
        startDate: Long,
        endDate: Long
    ): LiveData<RoomInfectedLocationEntity> {
        TODO("Add query to DB here")
    }
}
