package com.android_academy.covid_19.repository

import androidx.lifecycle.LiveData
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.network.service.InfectionDataService
import com.android_academy.covid_19.util.map
import timber.log.Timber

interface InfectionDataRepo {
    fun getInfectionLocations(startDate: Long, endDate: Long): LiveData<List<InfectedLocationModel>>
    suspend fun getInfectionLocationsAsync(
        startDate: Long,
        endDate: Long
    ): List<InfectedLocationModel>
}

class InfectionDataRepoImpl(
    private val dao: InfectionLocationsDao,
    private val service: InfectionDataService
) : InfectionDataRepo {

    override fun getInfectionLocations(
        startDate: Long,
        endDate: Long
    ): LiveData<List<InfectedLocationModel>> = dao.getInfectedLocationsByDatesRange()
        .map { infectedLocRoom ->
            infectedLocRoom.map { it.toInfectedLocationModel() }
        }

    override suspend fun getInfectionLocationsAsync(
        startDate: Long,
        endDate: Long
    ): List<InfectedLocationModel> {
        // here in the future we need to call for a server only for delta,
        // but currently we call for a complete data from server
        // val lastInfectedLocation =  dao.getLastUpdatedInfectedLocation()
        val fromServer = service.getInfectedLocations("covid-19",null, "carrier", "il")
        Timber.d("Got infected locations from server from server: $fromServer")
        dao.saveInfectedLocations(fromServer.locations.map { it.toDBModel() })
        return fromServer.locations
    }
}
