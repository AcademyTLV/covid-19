package com.android_academy.covid_19.ui.fragment.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_academy.covid_19.repository.UserMetaDataRepo
import com.android_academy.covid_19.repository.model.UserMetaData
import com.android_academy.covid_19.repository.model.UserType
import com.android_academy.covid_19.ui.fragment.intro.IntroViewModel.NavigationTarget
import com.android_academy.covid_19.ui.fragment.intro.IntroViewModel.NavigationTarget.Close
import com.android_academy.covid_19.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

interface IntroViewModel {

    fun nextAndSave(chosenType: UserType)

    val navigationTarget: LiveData<NavigationTarget>

    sealed class NavigationTarget {
        object Close : NavigationTarget()
    }
}

class IntroViewModelImpl(
    private val userMetaDataRepo: UserMetaDataRepo
) : ViewModel(), IntroViewModel {

    override val navigationTarget = SingleLiveEvent<NavigationTarget>()

    override fun nextAndSave(chosenType: UserType) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "Failed to save user type")
        }) {
            val currentUser = userMetaDataRepo.getCurrentUser()
            currentUser?.let {
                userMetaDataRepo.setUserType(chosenType)
            } ?: run {
                val user = UserMetaData(UUID.randomUUID().toString(), chosenType)
                userMetaDataRepo.setCurrentUser(user)
            }
            navigationTarget.value = Close
        }
    }
}
