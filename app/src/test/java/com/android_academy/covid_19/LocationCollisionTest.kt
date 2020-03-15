package com.android_academy.covid_19

import android.location.Location
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.providers.UserLocationModel
import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import java.util.Date


class LocationCollisionTest {
    @Test
    fun when_time_colliding_return_true() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584310947, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 0.0, 0.0, 0.0)

        //assert true
    }

    @Test
    fun when_no_time_colliding_return_true() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584302427, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 0.0, 0.0, 0.0)

        //assert false
    }


    @Test
    fun when_time_colliding_under_threshold_return_true() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584308727, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 0.0, 0.0, 0.0)

        //assert false
    }

    @Test
    fun when_location_colliding_return_true() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584310947, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 32.065405, 34.857194, 0.0)

        //assert true
    }

    @Test
    fun when_location_colliding_under_threshold_return_true() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584302427, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 32.065332, 34.857194, 0.0)
        Location.distanceBetween()

        //assert false
    }


    @Test
    fun when_no_location_colliding_return_false() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584308727, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 32.067423, 34.858986, 0.0)

        //assert false
    }
}
