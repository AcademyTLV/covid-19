package com.android_academy.covid_19

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android_academy.covid_19.network.service.InfectedLocationModel
import com.android_academy.covid_19.providers.UserLocationModel
import com.android_academy.covid_19.util.InfectionCollisionMatcherImpl
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun when_time_colliding_return_true() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584312974000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309374000), Date(1584316574000), 0.0, 0.0, 0.0)

        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )
        Assert.assertTrue(colliding.isNotEmpty())
    }

    @Test
    fun when_no_time_colliding_return_empty() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584312974000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309627), Date(1584313227), 0.0, 0.0, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )

        Assert.assertTrue(colliding.isEmpty())
    }


    @Test
    fun when_time_colliding_under_threshold_return_true() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584269100000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584270000000), Date(1584273540000), 0.0, 0.0, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )

        Assert.assertTrue(colliding.isNotEmpty())
    }


    @Test
    fun when_time_colliding_below_threshold_return_empty() {
        val userLocationModel = UserLocationModel(null, 0.0, 0.0, 0F, 0F, 1584267600000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584270000000), Date(1584273540000), 0.0, 0.0, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )

        Assert.assertTrue(colliding.isEmpty())
    }

    @Test
    fun when_location_colliding_return_true() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584312974000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309374000), Date(1584316574000), 32.065405, 34.857194, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )
        Assert.assertTrue(colliding.isNotEmpty())
    }

    @Test
    fun when_location_colliding_under_threshold_return_true() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584312974000, "")
        val coronaLocation =
            InfectedLocationModel(0,Date(1584309374000), Date(1584316574000), 32.065332, 34.857194, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )
        Assert.assertTrue(colliding.isNotEmpty())
    }


    @Test
    fun when_no_location_colliding_return_false() {
        val userLocationModel = UserLocationModel(null, 32.065405, 34.857194, 0F, 0F, 1584312974000, "")
        val coronaLocation =
            InfectedLocationModel(0, Date(1584309374000), Date(1584316574000), 32.067423, 34.858986, 0.0)
        val colliding = InfectionCollisionMatcherImpl().isColliding(
            listOf(coronaLocation),
            listOf(userLocationModel),
            30,
            30
        )
        Assert.assertTrue(colliding.isEmpty())
    }
}
