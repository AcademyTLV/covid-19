package com.android_academy.covid_19.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android_academy.covid_19.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LocationPermissionFragment: BottomSheetDialogFragment() {

    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    val REQUEST_LOCATION = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.location_permission_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        requestPermissions(permissions, REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun initView() {

    }

    companion object {
        val TAG = "LocationPermissionFragment"
        fun newInstance() =
            LocationPermissionFragment()
    }
}
