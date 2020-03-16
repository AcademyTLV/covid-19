package com.android_academy.covid_19.ui.fragment.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.repository.model.UserType
import com.android_academy.covid_19.ui.activity.MainViewModel
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import kotlinx.android.synthetic.main.intro_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroFragment : Fragment() {

    private val viewModel: IntroViewModel by viewModel<IntroViewModelImpl>()

    private val mainViewModel: MainViewModel by sharedViewModel<MainViewModelImpl>()

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
        initObservers()
    }

    private fun initObservers() {
        viewModel.apply {
            navigationTarget.observe(viewLifecycleOwner, Observer {
                when (it) {
                    IntroViewModel.NavigationTarget.Close -> {
                        mainViewModel.onUserSavedType()
                        activity?.onBackPressed()
                    }
                }
            })
        }
    }

    private fun initView() {
        introFragmentNextButton.isEnabled = false
        introRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            introFragmentNextButton.isEnabled = true
        }
        introFragmentNextButton.setOnClickListener {
            viewModel.nextAndSave(getTypeByCheckedButton())
        }
    }

    private fun getTypeByCheckedButton(): UserType {
        return when (val id = introRadioGroup.checkedRadioButtonId) {
            R.id.positive -> UserType.POSITIVE
            R.id.was_positive -> UserType.WAS_POSITIVE
            R.id.negative -> UserType.NEGATIVE
            else -> throw UnsupportedOperationException("Can not map selected button id: $id to UserType")
        }
    }

    companion object {
        fun newInstance() =
            IntroFragment()
    }
}
