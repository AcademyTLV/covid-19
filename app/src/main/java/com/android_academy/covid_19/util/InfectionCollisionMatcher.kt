package com.android_academy.covid_19.util

import android.location.Location
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.providers.UserLocationModel
import timber.log.Timber
import java.util.Date

interface InfectionCollisionMatcher {

    fun isColliding(
        coronaModels: List<InfectedLocationModel>,
        myLocations: List<UserLocationModel>,
        timeThreshold: Long,
        distanceThreshold: Int
    ): List<Pair<UserLocationModel, InfectedLocationModel>>
}

class InfectionCollisionMatcherImpl : InfectionCollisionMatcher {

    override fun isColliding(
        coronaModels: List<InfectedLocationModel>,
        myLocations: List<UserLocationModel>,
        timeThreshold: Long,
        distanceThreshold: Int
    ): List<Pair<UserLocationModel, InfectedLocationModel>> {
        val matches = mutableListOf<Pair<UserLocationModel, InfectedLocationModel>>()
        myLocations.forEach { myLocation ->
            coronaModels.forEach { corona ->
                if (isTimeColliding(myLocation, corona, timeThreshold) &&
                    isLocationColliding(myLocation, corona, distanceThreshold)
                ) matches.add(myLocation to corona)
            }
        }
        return matches
    }

    private fun isLocationColliding(
        myLocation: UserLocationModel,
        corona: InfectedLocationModel,
        distanceThreshold: Int
    ): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            myLocation.lat,
            myLocation.lon,
            corona.lat,
            corona.lon,
            results
        )
        return results[0] <= distanceThreshold
    }

    private fun isTimeColliding(
        myLocation: UserLocationModel,
        corona: InfectedLocationModel,
        timeThreshold: Long
    ): Boolean {
        // val myStartTime = DateTime.now().withMillis(myLocation.time).minusMinutes(8)
        // val myEndTime = DateTime.now().withMillis(myLocation.time).plusMinutes(8)
        //
        // val coronaStartTime = DateTime(corona.startTime)
        // val coronaEndTime = DateTime(corona.endTime)

        // if location coming from fuse it's has only single time, but from timeline it's actually frame of beginning and end
        if (myLocation.provider == "fake" && corona.startTime == Date(1584144000000) && corona.name == "גן משחקים, בן גוריון 3, כפר גבירול רחובות") {
            Timber.d("[InfectionCollisionMatcherImpl], isTimeColliding(): taking care of fake location")
        }
        val myStartTime = if (myLocation.timeStart != null && myLocation.timeStart != 0L) {
            myLocation.timeStart
        } else {
            myLocation.time?.minus(8 * 60_000)
        }

        val myEndTime = if (myLocation.timeEnd != null && myLocation.timeEnd != 0L) {
            myLocation.timeEnd
        } else {
            myLocation.time?.plus(8 * 60_000)
        }

        val coronaStartTime = corona.startTime.time - timeThreshold * 60 * 1_000
        val coronaEndTime = corona.endTime.time + timeThreshold * 60 * 1_000

        if (myStartTime in (coronaStartTime + 1) until coronaEndTime) return true
        if (myEndTime in (coronaStartTime + 1) until coronaEndTime) return true

        return false
    }
}
