package com.android_academy.covid_19.ui.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface FiltersViewModel {

    fun onStatusChangeClick()
    val startTimeLiveData: LiveData<String>
    val endTimeLiveData: LiveData<String>
    val dateLiveData: LiveData<Date>

    fun setDate(date: Date = Date())
    fun setStartTime(hour: Int, minute: Int)
    fun setEndTime(hour: Int, minute: Int)
    fun initialize()
}

class FiltersViewModelImpl : ViewModel(), FiltersViewModel {

    override val startTimeLiveData = MutableLiveData<String>()
    override val endTimeLiveData = MutableLiveData<String>()
    override val dateLiveData = MutableLiveData<Date>()

    override fun onStatusChangeClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        val calender = Calendar.getInstance()
        setStartTime(calender[Calendar.HOUR_OF_DAY], calender[Calendar.MINUTE])
        setDate()
    }

    override fun setDate(date: Date) {
        dateLiveData.postValue(date)
    }

    override fun setStartTime(hour: Int, minute: Int) {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)

        startTimeLiveData.postValue(formatTime(calender).toString())
        calender.add(Calendar.HOUR, 1)
        endTimeLiveData.postValue(formatTime(calender).toString())
    }

    override fun setEndTime(hour: Int, minute: Int) {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)

        endTimeLiveData.postValue(formatTime(calender).toString())
    }

    private fun formatTime(calender: Calendar) =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(calender.time)
}
