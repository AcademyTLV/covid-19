package com.android_academy.covid_19.repository

import androidx.lifecycle.LiveData
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.network.service.InfectionDataService
import com.android_academy.covid_19.repository.model.InfectedLocationModel

interface InfectionDataRepo {
    fun getInfectionLocations(startDate: Long, endDate: Long): LiveData<InfectedLocationModel>
}

class InfectionDataRepoImpl(
    private val dao: InfectionLocationsDao,
    private val service: InfectionDataService
) : InfectionDataRepo {

    override fun getInfectionLocations(
        startDate: Long,
        endDate: Long
    ): LiveData<InfectedLocationModel> {
        TODO("Add query to DB here")
    }
}
