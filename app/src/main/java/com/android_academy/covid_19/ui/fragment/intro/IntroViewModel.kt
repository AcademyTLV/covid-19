package com.android_academy.covid_19.ui.fragment.intro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.providers.LocationUpdateWorker
import com.android_academy.covid_19.repository.IUserMetaDataRepo
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.ui.fragment.main.MainNavigationTarget.GoogleLoginView
import com.android_academy.covid_19.util.SingleLiveEvent
import kotlinx.coroutines.launch

interface IntroViewModel {
    fun onButtonChosen(chosenType : String)
    fun nextAndSave()
}

class IntroViewModelImpl(private val userMetaDataRepo: IUserMetaDataRepo) : ViewModel(), IntroViewModel {

    var chosenType : String? = null

    override fun onButtonChosen(chosenType : String) {
        Log.d("omer", "chosen ${chosenType}")
        this.chosenType = chosenType
    }

    override fun nextAndSave() {
        Log.d("omer", "beforeSave ${chosenType}")
        chosenType?.let {
            Log.d("omer", "clicked when chosen ${chosenType}")
            viewModelScope.launch {
                userMetaDataRepo.setUserType(it)
            }
            viewModelScope.launch {
                val user = userMetaDataRepo.getUserMetaData()
                Log.d("omer", "fetched ${user.type}")
            }
        }
    }
}

