package com.android_academy.covid_19.ui.fragment.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.fragment.main.UsersLocationListViewModelImpl
import com.android_academy.covid_19.ui.fragment.main.UsersLocationViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.intro_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroFragment : Fragment() {

    private val viewModel: IntroViewModel by viewModel<IntroViewModelImpl>()

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
        arrayOf<Button>(wasPositiveButton, notPositiveButton, positiveButton).forEach {
            it.setOnClickListener(::onChosenType)
        }

        introFragmentNextButton.setOnClickListener {
            viewModel.nextAndSave()
        }
    }

    private fun onChosenType(view: View) {
        arrayOf<Button>(wasPositiveButton, notPositiveButton, positiveButton).forEach {
            if (it != view){
                (it as MaterialButton).isChecked = false
            }
        }

        Utils.getTypeByButton(view.id)?.let {
            viewModel.onButtonChosen(it)
        }
    }

    companion object {
        val TAG = "LocationPermissionFragment"
        fun newInstance() =
            IntroFragment()
    }
}
