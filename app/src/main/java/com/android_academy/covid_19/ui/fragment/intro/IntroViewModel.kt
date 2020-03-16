package com.android_academy.covid_19.ui.fragment.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.R
import com.android_academy.covid_19.repository.IUserMetaDataRepo
import kotlinx.coroutines.async
import timber.log.Timber

data class IntroModel(var chosenType: String? = null)

interface IntroViewModel {
    fun onButtonChosen(chosenType: String)
    fun nextAndSave()
}

class IntroViewModelImpl(
    private val userMetaDataRepo: IUserMetaDataRepo,
    private val introModel: IntroModel = IntroModel()
) : ViewModel(), IntroViewModel {

    override fun onButtonChosen(chosenType: String) {
        introModel.chosenType = chosenType
        Timber.d("chosen ${introModel.chosenType}")
    }

    override fun nextAndSave() {
        Timber.d("beforeSave ${introModel.chosenType}")
        introModel.chosenType?.let {
            Timber.d("clicked when chosen ${it}")
            viewModelScope.async {
                userMetaDataRepo.setUserType(it)
            }

            // Need to test the value, but need to wait for promise to finish
            val userPromise = viewModelScope.async {
                userMetaDataRepo.getUserMetaData()
            }
        }
    }

}

