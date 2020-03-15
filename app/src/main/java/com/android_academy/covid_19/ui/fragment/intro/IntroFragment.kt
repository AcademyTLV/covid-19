package com.android_academy.covid_19.ui.fragment.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android_academy.covid_19.R

class IntroFragment: Fragment() {

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
