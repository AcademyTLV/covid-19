package com.android_academy.covid_19.repository

import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.network.service.InfectionDataService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface InfectionDataRepo {
    fun getInfectionLocations(): Flow<List<InfectedLocationModel>>
    suspend fun getInfectionLocationsAsync(
        startDate: Long,
        endDate: Long
    ): List<InfectedLocationModel>
}

class InfectionDataRepoImpl(
    private val dao: InfectionLocationsDao,
    private val service: InfectionDataService
) : InfectionDataRepo {

    override fun getInfectionLocations(): Flow<List<InfectedLocationModel>> =
        dao.getAllInfectedLocations()
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
        val fromServer = service.getInfectedLocationsMOH().toInfectedLocations()
        Timber.d("Got infected locations from server from server: $fromServer")
        dao.deleteOldLocations()
        dao.saveInfectedLocations(fromServer.map { it.toDBModel() })
        return fromServer
    }
}
