package com.android_academy.covid_19.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android_academy.covid_19.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IntroFragment: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        // btnLogin.setOnClickListener {
        //     // viewModel.onLoginClick()
        //     val intent = Intent(context, MapsActivity::class.java)
        //     startActivity(intent)
        // }
    }

    companion object {
        val TAG = "LocationPermissionFragment"
        fun newInstance() =
            IntroFragment()
    }
}
