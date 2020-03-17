package com.android_academy.covid_19.ui.activity

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
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
import com.android_academy.covid_19.ui.map.MapManager
import com.android_academy.covid_19.util.setSafeOnClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val LOCATION_SETTINGS_SCREEN_CODE = 1002

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModelImpl> {
        parametersOf(
            hasLocationPermissions()
        )
    }

    private val mapManager: MapManager by inject {
        parametersOf(
            viewModel as MapManager.InteractionInterface
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
        mapFragment.getMapAsync(mapManager)
    }

    private fun initObservers() {
        viewModel.apply {
            navigation.observe(this@MainActivity, onNavigationChanged())
            myLocations.observe(this@MainActivity, Observer { mapManager.onMyLocationsChanged(it) })
            coronaLocations.observe(this@MainActivity, Observer { mapManager.onCoronaChanged(it) })
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
        ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION,
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
            runWithPermissions(READ_EXTERNAL_STORAGE) {
                viewModel.onTimelineTriggerClicked()
            }
        }
    }
}
