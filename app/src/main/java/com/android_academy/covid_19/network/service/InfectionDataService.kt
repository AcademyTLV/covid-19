package com.android_academy.covid_19.network.service

import com.android_academy.covid_19.network.response.InfectedLocationsMOHResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InfectionDataService {

    @GET("/v1/events/{event_id}/locations")
    suspend fun getInfectedLocations(
        @Path("event_id") eventId: String = "covid-19",
        @Query("min_time") minTime: String? = null,
        @Query("patient_status") patientStatus: String = "carrier",
        @Query("country") country: String = "il"
    ): InfectedLocations

    @GET("https://services5.arcgis.com/dlrDjz89gx9qyfev/ArcGIS/rest/services/Corona_Exposure_View/FeatureServer/0/query?where=1%3D1&objectIds=&time=&geometry=&geometryType=esriGeometryEnvelope&inSR=&spatialRel=esriSpatialRelIntersects&resultType=none&distance=0.0&units=esriSRUnit_Meter&returnGeodetic=false&outFields=*&returnGeometry=true&featureEncoding=esriDefault&multipatchOption=xyFootprint&maxAllowableOffset=&geometryPrecision=&outSR=&datumTransformation=&applyVCSProjection=false&returnIdsOnly=false&returnUniqueIdsOnly=false&returnCountOnly=false&returnExtentOnly=false&returnQueryGeometry=false&returnDistinctValues=false&cacheHint=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&having=&resultOffset=&resultRecordCount=&returnZ=false&returnM=false&returnExceededLimitFeatures=true&quantizationParameters=&sqlFormat=none&f=pgeojson&token=")
    suspend fun getInfectedLocationsMOH(): InfectedLocationsMOHResponse
}
