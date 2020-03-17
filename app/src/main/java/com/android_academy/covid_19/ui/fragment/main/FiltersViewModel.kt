package com.android_academy.covid_19.ui.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FilterDates(
    val startDate: Date,
    val endDate: Date
)

interface FiltersViewModel {

    fun onStatusChangeClick()
    val startTimeLiveData: LiveData<String>
    val endTimeLiveData: LiveData<String>
    val dateLiveData: LiveData<Date>
    val filterDatesSet: LiveData<FilterDates>

    fun setDate(date: Date = Date())
    fun getDate(): Date?
    fun getTimeStart(): Date?
    fun getTimeEnd(): Date?
    fun setStartTime(hour: Int, minute: Int)
    fun setEndTime(hour: Int, minute: Int)
    fun initialize()
}

class FiltersViewModelImpl : ViewModel(), FiltersViewModel {

    override val startTimeLiveData = MutableLiveData<String>()
    override val endTimeLiveData = MutableLiveData<String>()
    override val dateLiveData = MutableLiveData<Date>()
    override val filterDatesSet = MutableLiveData<FilterDates>()
    private var dateTimeStart: Date? = null
    private var dateTimeEnd: Date? = null

    override fun onStatusChangeClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        val calender = Calendar.getInstance()
        setStartTime(calender[Calendar.HOUR_OF_DAY] - 1, calender[Calendar.MINUTE])
        setDate(calender.time)

        // calender.add(Calendar.HOUR, 1)
        // dateTimeEnd = calender.time
        // val start = dateTimeStart
        // val end = dateTimeEnd
        // if ((start != null) && (end != null)) {
        //     filterDatesSet.value = FilterDates(
        //         startDate = start,
        //         endDate = end
        //     )
        // }

    }

    override fun setDate(date: Date) {
        dateLiveData.postValue(date)
    }

    override fun getDate(): Date? {
        return dateLiveData.value
    }

    override fun getTimeStart(): Date? {
        return dateTimeStart
    }

    override fun getTimeEnd(): Date? {
        return dateTimeEnd
    }

    override fun setStartTime(hour: Int, minute: Int) {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)
        dateTimeStart = calender.time

        startTimeLiveData.postValue(formatTime(calender).toString())

        if ((dateTimeEnd == null) || (dateTimeEnd?.before(dateTimeStart)!!)){
            calender.add(Calendar.HOUR, 1)
            dateTimeEnd = calender.time

            endTimeLiveData.postValue(formatTime(calender).toString())
        }

        val start = dateTimeStart
        val end = dateTimeEnd
        if ((start != null) && (end != null)) {
            filterDatesSet.value = FilterDates(
                startDate = start,
                endDate = end
            )
        }
    }

    override fun setEndTime(hour: Int, minute: Int) {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, minute)
        dateTimeEnd = calender.time
        endTimeLiveData.postValue(formatTime(calender).toString())

        val start = dateTimeStart
        val end = dateTimeEnd
        if ((start != null) && (end != null)) {
            filterDatesSet.value = FilterDates(
                startDate = start,
                endDate = end
            )
        }
    }

    private fun formatTime(calender: Calendar) =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(calender.time)

}
