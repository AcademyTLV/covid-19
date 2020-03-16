package com.android_academy.covid_19.network.response

import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Feature(
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("id")
    val id: Int,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("type")
    val type: String
)

data class Properties(
    @SerializedName("Comments")
    val comments: String,
    @SerializedName("fromTime")
    val fromTime: Long,
    @SerializedName("Name")
    val name: String,
    @SerializedName("OBJECTID")
    val oBJECTID: Int,
    @SerializedName("POINT_X")
    val pOINTX: Double,
    @SerializedName("POINT_Y")
    val pOINTY: Double,
    @SerializedName("Place")
    val place: String,
    @SerializedName("sourceOID")
    val sourceOID: Any?,
    @SerializedName("stayTimes")
    val stayTimes: String,
    @SerializedName("toTime")
    val toTime: Long
)

data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<Double>,
    @SerializedName("type")
    val type: String
)

data class InfectedLocationsMOHResponse(
    @SerializedName("features")
    val features: List<Feature>,
    @SerializedName("type")
    val type: String
) {
    fun toInfectedLocationsResponse(): InfectedLocationsResponse {
        val nexarModels = features.map { mohModel ->
            InfectedLocationModel(
                id = mohModel.id,
                startTime = Date(mohModel.properties.fromTime),
                endTime = Date(mohModel.properties.toTime),
                lat = mohModel.geometry.coordinates[0],
                lon = mohModel.geometry.coordinates[1],
                radius = 30.0
            )
        }
        return InfectedLocationsResponse(nexarModels)
    }
}
