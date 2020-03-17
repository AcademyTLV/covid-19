package com.android_academy.covid_19.ui.activity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.Settings
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.LocationSettingsScreen
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.PermissionsBottomSheetExplanation
import com.android_academy.covid_19.ui.fragment.LocationPermissionFragment
import com.android_academy.covid_19.ui.fragment.intro.IntroFragment
import com.android_academy.covid_19.util.setSafeOnClickListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val LOCATION_SETTINGS_SCREEN_CODE = 1002

private data class MarkerAndCircle(
    val marker: Marker,
    val circle: Circle
)

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val myLocations = mutableMapOf<Int, MarkerAndCircle>()

    private val coronaLocations = mutableMapOf<Int, Marker>()

    private var selectedCoronaLocation: Marker? = null

    private val viewModel: MainViewModel by viewModel<MainViewModelImpl> {
        parametersOf(
            hasLocationPermissions()
        )
    }

    private fun hasLocationPermissions(): Boolean {
        return checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
        initViews()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onScreenBecameVisible()
    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initObservers() {
        viewModel.apply {
            navigation.observe(this@MainActivity, onNavigationChanged())
            myLocations.observe(this@MainActivity, Observer { onMyLocationsChanged(it) })
            coronaLocations.observe(this@MainActivity, Observer { onCoronaChanged(it) })
            blockingUIVisible.observe(this@MainActivity, Observer {
                showBlockUI(it)
            })
            error.observe(this@MainActivity, Observer {
                Toast.makeText(this@MainActivity, R.string.something_went_wrong, Toast.LENGTH_LONG)
                    .show()
            })
            locationPermissionCheck.observe(this@MainActivity, Observer {
                it?.let {
                    val options = QuickPermissionsOptions(
                        handleRationale = true,
                        rationaleMessage = getString(R.string.location_dialog_description),
                        permanentlyDeniedMessage = getString(R.string.decline_permission_bottom_sheet_dialog_btn),
                        rationaleMethod = { req -> req.proceed() },
                        permanentDeniedMethod = { req ->
                            req.cancel()
                            viewModel.onUserPermanentlyDeniedPermission()
                        }
                    )
                    requestLocationPermissions(options)
                }
            })
        }
    }

    private fun onMyLocationsChanged(markerOptions: List<LocationMarkerData>?) {
        markerOptions?.forEach { options ->
            myLocations[options.id]?.let {
                it.marker.remove()
                it.circle.remove()
            }
            myLocations[options.id] =
                MarkerAndCircle(
                    marker = map.addMarker(createMyLocationMarkerOptions(options)),
                    circle = map.addCircle(createMyLocationCircleOptions(options))
                )
        }
    }

    private fun onCoronaChanged(markerOptions: List<LocationMarkerData>?) {
        val coronaIcon = getCoronaMarkerBitmap(selected = false)
        markerOptions?.forEach { options ->
            coronaLocations[options.id]?.let {
                it.remove()
            }

            val marker = map.addMarker(createCoronaLocationMarkerOptions(options, coronaIcon))
            marker.tag = options
            coronaLocations[options.id] = marker
        }
    }

    private fun getCoronaMarkerBitmap(selected: Boolean): Bitmap {
        val drawable =
            resources.getDrawable(if (selected) R.drawable.corona_circle_shape_selected else R.drawable.corona_circle_shape_not_selected)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createMyLocationCircleOptions(options: LocationMarkerData): CircleOptions {
        return CircleOptions()
            .center(LatLng(options.lat, options.lon))
            .radius(500.0)
            .fillColor(R.color.colorPrimaryDark_30)
    }

    private fun createCoronaLocationCircleOptions(options: LocationMarkerData): CircleOptions {
        return CircleOptions()
            .center(LatLng(options.lat, options.lon))
            .radius(500.0)
            .fillColor(R.color.orange_30)
    }

    private fun createMyLocationMarkerOptions(options: LocationMarkerData): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
    }

    private fun createCoronaLocationMarkerOptions(
        options: LocationMarkerData,
        coronaIcon: Bitmap
    ): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .icon(BitmapDescriptorFactory.fromBitmap(coronaIcon))
            .alpha(0.1F)
    }

    private fun showBlockUI(show: Boolean) {
        blockingUILayout.visibility = if (show) VISIBLE else GONE
    }

    private fun requestLocationPermissions(options: QuickPermissionsOptions) = runWithPermissions(
        ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        options = options
    ) {
        viewModel.onPermissionGranted()
    }

    private fun onNavigationChanged(): Observer<in MainNavigationTarget> = Observer {
        when (it) {
            MainNavigationTarget.IntroFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.overlayContainer,
                        IntroFragment.newInstance(),
                        IntroFragment::class.java.simpleName
                    )
                    .addToBackStack(null)
                    .commit()
            }
            PermissionsBottomSheetExplanation -> {
                // prevent showing if already shown
                supportFragmentManager.findFragmentByTag(LocationPermissionFragment.TAG)
                    ?.run { return@Observer }
                LocationPermissionFragment.newInstance()
                    .show(supportFragmentManager, LocationPermissionFragment.TAG)
            }
            LocationSettingsScreen -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, LOCATION_SETTINGS_SCREEN_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_SETTINGS_SCREEN_CODE -> {
                viewModel.onReturnedFromLocationSettings(hasLocationPermissions())
            }
        }
    }

    private fun initViews() {
        goToSettingsButton.setSafeOnClickListener {
            viewModel.onGoToSettingsClick()
        }

        button_trigger_timeline.setOnClickListener {
            viewModel.onTimelineTriggerClicked()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener { clicked ->
            redrawClickedCoronaMarker(clicked)
        }
    }

    private fun redrawClickedCoronaMarker(clicked: Marker): Boolean {
        if (clicked.tag == selectedCoronaLocation?.tag) return false

        selectedCoronaLocation?.let {
            selectMarker(it, false)
        }

        val new = selectMarker(clicked, true)
        selectedCoronaLocation = new

        return false
    }

    private fun selectMarker(marker: Marker, isSelect: Boolean): Marker {

        // Add new marker on the map, based on old, but with unselected drawable
        val selectedIcon = getCoronaMarkerBitmap(selected = isSelect)
        val new = map.addMarker(
            createCoronaLocationMarkerOptions(
                marker.tag as LocationMarkerData,
                selectedIcon
            )
        ).apply { tag = marker.tag }

        // Remove from map
        marker.remove()

        return new
    }
}
