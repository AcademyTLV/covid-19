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
        .map {
            it.map { it.toInfectedLocationModel() }
        }

    override suspend fun getInfectionLocationsAsync(
        startDate: Long,
        endDate: Long
    ): List<InfectedLocationModel> {
        // here in the future we need to call for a server only for delta,
        // but currently we call for a complete data from server
        // val lastInfectedLocation =  dao.getLastUpdatedInfectedLocation()
        val fromServer = service.getInfectedLocationsMOH()
        Timber.d("Got infected locations from server from server: $fromServer")
        dao.saveInfectedLocations(fromServer.toInfectedLocationsResponse().locations.map { it.toDBModel() })
        return fromServer.toInfectedLocationsResponse().locations
    }
}
