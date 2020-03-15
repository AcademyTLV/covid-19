package com.android_academy.covid_19.network.response

import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.google.gson.annotations.SerializedName

data class InfectedLocationsResponse(

    @SerializedName("locations")
    val locations: List<InfectedLocationModel>
)
