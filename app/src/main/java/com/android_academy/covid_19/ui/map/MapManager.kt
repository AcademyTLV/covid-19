package com.android_academy.covid_19.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.LocationMarkerData
import com.android_academy.covid_19.ui.map.MapManager.InteractionInterface
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

interface MapManager : OnMapReadyCallback {

    fun onMyLocationsChanged(markerOptions: List<LocationMarkerData>?)
    fun onCoronaChanged(markerOptions: List<LocationMarkerData>?)
    fun onCollisionChanged(markers: Pair<LocationMarkerData, LocationMarkerData>)

    interface InteractionInterface {
        fun onUserHistoryLocationMarkerSelected(data: LocationMarkerData)
        fun onMapReady()
    }
}

class MapManagerImpl(
    private val interactionInterface: InteractionInterface,
    private val context: Context,
    private val scope: CoroutineScope
) : MapManager {

    private lateinit var map: GoogleMap

    private val myLocations = mutableMapOf<Int, Marker>()

    private val coronaLocations = mutableMapOf<Int, Marker>()

    private var selectedCoronaLocation: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        interactionInterface.onMapReady()
        map.setOnMarkerClickListener { clicked ->

            if (clicked.tag == null || clicked.tag == selectedCoronaLocation?.tag) return@setOnMarkerClickListener false

            selectedCoronaLocation?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.not_selected_circle))
            clicked.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_circle))
            selectedCoronaLocation = clicked
            interactionInterface.onUserHistoryLocationMarkerSelected(clicked.tag as LocationMarkerData)

            return@setOnMarkerClickListener false
        }

        val pos = LatLng(31.784958, 34.921960)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 7.3F))

        if (ContextCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onMyLocationsChanged(markerOptions: List<LocationMarkerData>?) {
        markerOptions?.forEach { options ->
            myLocations[options.id]?.remove()
            Timber.d("[MapManagerImpl], onMyLocationsChanged(): adding user location marker $options")
            myLocations[options.id] = map.addMarker(createMyLocationMarkerOptions(options))
        }
    }

    override fun onCoronaChanged(markerOptions: List<LocationMarkerData>?) {
        markerOptions?.let { new ->

            coronaLocations.forEach {
                it.value.remove()
            }
            coronaLocations.clear()

            new.forEach { options ->
                val marker = map.addMarker(
                    createCoronaLocationMarkerOptions(
                        options,
                        R.drawable.not_selected_circle
                    )
                )
                marker.tag = options
                coronaLocations[options.id] = marker
            }
        }
    }

    override fun onCollisionChanged(markers: Pair<LocationMarkerData, LocationMarkerData>) {
        map.clear()
        map.addMarker(createMyLocationMarkerOptions(markers.first))
        map.addMarker(
            createCoronaLocationMarkerOptions(
                markers.second,
                R.drawable.not_selected_circle
            )
        )
        map.setPadding(0,0,0, ((230 * Resources.getSystem().displayMetrics.density).toInt()))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(markers.first.lat, markers.first.lon),14f))
    }

    private fun createMyLocationMarkerOptions(options: LocationMarkerData): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .snippet(options.snippet)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_icon))
    }

    private fun createCoronaLocationMarkerOptions(
        options: LocationMarkerData,
        coronaIcon: Int
    ): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .snippet(options.snippet)
            .icon(BitmapDescriptorFactory.fromResource(coronaIcon))
    }
}
