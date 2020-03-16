package com.android_academy.covid_19.ui.activity

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.ui.fragment.LocationPermissionFragment
import com.android_academy.covid_19.ui.fragment.intro.IntroFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val viewModel: MainViewModel by viewModel<MainViewModelImpl>()

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        Toast.makeText(this, "Give me fucking permission!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initViews(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.apply {
            navigation.observe(this@MainActivity, onNavigationChanged())
            error.observe(this@MainActivity, Observer {
                Toast.makeText(this@MainActivity, R.string.something_went_wrong, Toast.LENGTH_LONG)
                    .show()
            })
            startMyLocationPeriodicJob.observe(this@MainActivity, Observer {
                it?.let {
                    val options = QuickPermissionsOptions(
                        handleRationale = false,
                        rationaleMessage = "We need your location access, in order to be able to compare if you was near infected people",
                        permanentlyDeniedMessage = "You will not be able to use an app without a location permission",
                        rationaleMethod = { req -> rationaleCallback(req) },
                        permanentDeniedMethod = { req -> rationaleCallback(req) }
                    )
                    onStartMyLocationPeriodicJob(options)
                }
            })
        }
    }

    private fun onStartMyLocationPeriodicJob(options: QuickPermissionsOptions) = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        options = options
    ) {
        LocationUpdateWorker.schedule()
        viewModel.onStartedMyLocationPeriodicJob()
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
        }
    }

    /* This is an attempts to use CustomTabs to sign in to Google*/
    private fun openLoginWebView() {
        val builder = CustomTabsIntent.Builder()
        // builder.setSession(session)
        builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
        // Application exit animation, Chrome enter animation.
        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
        // vice versa
        builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)

        val pendingIntent =
            PendingIntent.getActivity(this, 1001, Intent(this, MainActivity::class.java), 0)
        builder.setActionButton(
            resources.getDrawable(R.drawable.ic_launcher_foreground).toBitmap(),
            "hmmm",
            pendingIntent
        )

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            this,
            // Uri.parse("https://www.google.com/maps/timeline/kml?authuser=1&pb=!1m8!1m3!1i2020!2i2!3i14!2m3!1i2020!2i2!3i14")
            Uri.parse("https://www.google.com")
        )

        CustomTabsClient.bindCustomTabsService(
            this,
            "com.android.chrome",
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    client: CustomTabsClient
                ) {
                    Log.d("XXX", "onCustomTabsServiceConnected")
                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                    Log.d("XXX", "onServiceDisconnected")
                }
            })
    }

    private fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // supportFragmentManager.beginTransaction()
            //     //.replace(R.id.container, IntroFragment.newInstance())
            //     .replace(R.id.container,
            //         UsersLocationListFragment()
            //     )
            //     .commitNow()
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

    fun askLocationPermission() {
        LocationPermissionFragment.newInstance()
            .show(supportFragmentManager, LocationPermissionFragment.TAG)
    }
}
