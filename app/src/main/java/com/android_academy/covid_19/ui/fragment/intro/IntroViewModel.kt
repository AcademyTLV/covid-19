package com.android_academy.covid_19.ui.fragment.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.ui.fragment.main.MainNavigationTarget.GoogleLoginView
import com.android_academy.covid_19.util.SingleLiveEvent

interface IntroViewModel {
    val onButtonChosen: LiveData<String>
    fun onNextClicked(chosenType : String)
}

class IntroViewModelImpl : ViewModel(), IntroViewModel {

    override val resultTextView = MutableLiveData<String>()
    val onButtonChosen: LiveData<String>
    override fun onNextClicked() {
        LocationUpdateWorker.schedule()
    }
}
