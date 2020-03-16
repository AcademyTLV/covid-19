package com.android_academy.covid_19.network.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InfectionDataService {

    @GET("/v1/events/{event_id}/locations")
    suspend fun getInfectedLocations(
        @Path("event_id") eventId: String,
        @Query("min_time") minTime: String? = null,
        @Query("patient_status") patientStatus: String,
        @Query("country") country: String
    ): InfectedLocations
}
