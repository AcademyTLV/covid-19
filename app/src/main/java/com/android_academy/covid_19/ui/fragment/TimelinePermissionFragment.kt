package com.android_academy.covid_19.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.location_permission_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TimelinePermissionFragment : BottomSheetDialogFragment() {

    private val mainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.timeline_permission_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        skipPermissionButton.setOnClickListener {
            dismiss()
        }
        givePermissionButton.setOnClickListener {
            runWithPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
                mainViewModel.onTimelineTriggerClicked()
                dismiss()
            }
        }
    }

    companion object {
        val TAG = "TimelinePermissionFragment"
        fun newInstance() =
            TimelinePermissionFragment().apply { isCancelable = false }
    }
}
