package com.android_academy.covid_19.ui.activity

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.provider.Settings
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.LocationSettingsScreen
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.PermissionsBottomSheetExplanation
import com.android_academy.covid_19.ui.activity.MainNavigationTarget.StoragePermissionGranted
import com.android_academy.covid_19.ui.fragment.ChangeStatusFragment
import com.android_academy.covid_19.ui.fragment.InfectionMatchFragment
import com.android_academy.covid_19.ui.fragment.LocationPermissionFragment
import com.android_academy.covid_19.ui.fragment.TimelinePermissionFragment
import com.android_academy.covid_19.ui.fragment.intro.IntroFragment
import com.android_academy.covid_19.ui.map.MapManager
import com.android_academy.covid_19.util.setSafeOnClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
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
            viewModel as MapManager.InteractionInterface,
            lifecycleScope
        )
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            hasLocationPermissionsPreQ() else
            hasLocationPermissionsQ()
    }

    private fun hasLocationPermissionsQ(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            (ACCESS_COARSE_LOCATION)
        ) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, (ACCESS_FINE_LOCATION)) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                (ACCESS_BACKGROUND_LOCATION)
            ) == PERMISSION_GRANTED
    }

    private fun hasLocationPermissionsPreQ(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            (ACCESS_COARSE_LOCATION)
        ) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, (ACCESS_FINE_LOCATION)) == PERMISSION_GRANTED
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
                    askForegroundPermissions()
                }
            })
        }
    }

    private fun askForegroundPermissions() {
        val foregroundPermissionsOptions = QuickPermissionsOptions(
            permanentDeniedMethod = { req ->
                viewModel.onUserPermanentlyDeniedLocationPermission()
            },
            permissionsDeniedMethod = { req ->
                viewModel.onUserDeniedOneOfTheLocationPermissions()
            }
        )
        runWithPermissions(
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION, options = foregroundPermissionsOptions
        ) {
            if (Build.VERSION.SDK_INT < Q) {
                viewModel.onLocationPermissionGranted()
            } else {
                askBackgroundPermissions()
            }
        }
    }

    @TargetApi(Q)
    private fun askBackgroundPermissions() {
        val options = QuickPermissionsOptions(
            permanentDeniedMethod = { req ->
                viewModel.onUserPermanentlyDeniedLocationPermission()
            },
            permissionsDeniedMethod = { req ->
                viewModel.onUserDeniedOneOfTheLocationPermissions()
            }
        )
        runWithPermissions(
            ACCESS_BACKGROUND_LOCATION, options = options
        ) {
            viewModel.onLocationPermissionGranted()
        }
    }

    private fun showBlockUI(show: Boolean) {
        blockingUILayout.visibility = if (show) VISIBLE else GONE
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
            MainNavigationTarget.ChangeStatusBottomSheet -> {
                // prevent showing if already shown
                supportFragmentManager.findFragmentByTag(ChangeStatusFragment.TAG)
                    ?.run { return@Observer }
                ChangeStatusFragment.newInstance()
                    .show(supportFragmentManager, ChangeStatusFragment.TAG)
            }
            MainNavigationTarget.TimelineBottomSheetExplanation -> {
                // prevent showing if already shown
                supportFragmentManager.findFragmentByTag(TimelinePermissionFragment.TAG)
                    ?.run { return@Observer }
                TimelinePermissionFragment.newInstance()
                    .show(supportFragmentManager, TimelinePermissionFragment.TAG)
            }
            LocationSettingsScreen -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, LOCATION_SETTINGS_SCREEN_CODE)
            }
            StoragePermissionGranted -> {
                viewModel.onTimelineTriggerClicked()
            }

            MainNavigationTarget.InfectionMatchGallery -> {
                supportFragmentManager.findFragmentByTag(InfectionMatchFragment.TAG)
                    ?.run { return@Observer }
                InfectionMatchFragment.newInstance()
                    .show(supportFragmentManager, InfectionMatchFragment.TAG)
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
            viewModel.onGoToLocationSettingsClick()
        }

        // button_trigger_timeline.setOnClickListener {
        //     val options = QuickPermissionsOptions(
        //         handlePermanentlyDenied = true,
        //         permanentDeniedMethod = { req ->
        //             showStoragePermanentlyDeniedDialog(req)
        //         }
        //     )
        //     runWithPermissions(READ_EXTERNAL_STORAGE, options = options) {
        //         viewModel.onTimelineTriggerClicked()
        //     }
        // }
    }

    private fun showStoragePermanentlyDeniedDialog(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(R.string.storage_dialog_title)
            .setMessage(R.string.storage_dialog_subtitle)
            .setPositiveButton(R.string.storage_dialog_positive_btn) { dialog, _ ->
                req.openAppSettings()
            }
            .setNegativeButton(R.string.storage_dialog_negative_btn) { dialog, _ ->
                req.cancel()
            }
            .show()
            .setCancelable(false)
    }
}
