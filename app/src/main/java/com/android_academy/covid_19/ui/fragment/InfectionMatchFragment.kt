package com.android_academy.covid_19.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.CollisionLocationModel
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.infection_match_fragment.*
import kotlinx.android.synthetic.main.location_permission_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InfectionMatchFragment : BottomSheetDialogFragment() {
    private val mainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.infection_match_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mainViewModel.collisionLocations.observe(viewLifecycleOwner, Observer {
           match_title.text = it.joinToString()
       })
    }

    companion object {
        val TAG = "InfectionMatchFragment"
        fun newInstance() =
            InfectionMatchFragment().apply { isCancelable = true }
    }
}
