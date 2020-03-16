package com.android_academy.covid_19.ui.activity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val LOCATION_SETTINGS_SCREEN_CODE = 1002

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

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

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initObservers() {
        viewModel.apply {
            navigation.observe(this@MainActivity, onNavigationChanged())
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
