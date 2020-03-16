package com.android_academy.covid_19.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.location_permission_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LocationPermissionFragment : BottomSheetDialogFragment() {
    private val mainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.location_permission_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        skipPermissionButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.decline_permission_bottom_sheet_dialog_btn),
                Toast.LENGTH_LONG
            ).show()
        }
        givePermissionButton.setOnClickListener {
            mainViewModel.onUserAcceptedLocationRequestExplanation()
            dismiss()
        }
    }

    companion object {
        val TAG = "LocationPermissionFragment"
        fun newInstance() =
            LocationPermissionFragment().apply { isCancelable = false }
    }
}
