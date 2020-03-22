package com.android_academy.covid_19.ui.fragment.main

import android.app.Application
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.DatePicker
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.android_academy.covid_19.ui.fragment.main.FiltersViewModel.NavigationTarget
import com.android_academy.covid_19.util.SingleLiveEvent
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import org.joda.time.DateTime
import timber.log.Timber
import java.util.Date

data class TimeRangeProgress(
    val start: Float,
    val end: Float
)

interface FiltersViewModel : OnDateSetListener, OnRangeChangedListener {

    val onDatesSet: LiveData<Pair<Date, Date>>
    val navigationTarget: LiveData<NavigationTarget>
    val date: LiveData<Date>
    val startTime: LiveData<Date>
    val endTime: LiveData<Date>
    val progress: LiveData<TimeRangeProgress>

    fun onDatePickerClick()
    fun onTimeRangeBtnClick()

    fun initialize()

    sealed class NavigationTarget {
        data class DatePicker(
            val date: Date
        ) : NavigationTarget()
    }
}

class FiltersViewModelImpl(
    application: Application
) : AndroidViewModel(application), FiltersViewModel {

    override val date = MutableLiveData<Date>()

    override val navigationTarget = SingleLiveEvent<NavigationTarget>()

    override val startTime = MediatorLiveData<Date>()

    override val endTime = MediatorLiveData<Date>()

    override val progress = MediatorLiveData<TimeRangeProgress>()

    override val onDatesSet = MutableLiveData<Pair<Date, Date>>()

    init {
        initialize()
    }

    override fun initialize() {
        val initial = getInitialDate()
        date.value = initial
        initTimes(initial)
        updateDateTimesSet()
    }

    private fun updateDateTimesSet() {
        val startDateTime = date.value!!.toJoda()
            .withTime(startTime.value!!.toJoda().toLocalTime())
            .toDate()
        val endDateTime = date.value!!.toJoda()
            .withTime(endTime.value!!.toJoda().toLocalTime())
            .toDate()
        onDatesSet.value = Pair(startDateTime, endDateTime)
    }

    private fun initTimes(initialDate: Date) {
        val joda = initialDate.toJoda()
        val time_00_00 = joda
            .withTimeAtStartOfDay()

        endTime.value = joda.toDate()

        /**
         * Uncomment this code if we want to show 1 hour back
         * */
        // val time_01_00 = time_00_00
        // .plusHours(1)
        // val startTime: Date = when {
        //     joda.isAfter(time_00_00) && joda.isBefore(time_01_00) -> {
        //         time_00_00.toDate()
        //     }
        //     else -> {
        //         joda.minusHours(1).toDate()
        //     }
        // }

        // Initial time always from the start of the day
        val startTime = time_00_00.toDate()
        this@FiltersViewModelImpl.startTime.value = startTime

        val progress = TimeRangeProgress(
            end = getTimeRangeProgress(joda.toDate()),
            start = getTimeRangeProgress(startTime)
        )
        this@FiltersViewModelImpl.progress.value = progress
    }

    private fun getTimeRangeProgress(date: Date): Float {
        val joda = DateTime(date)
        val minuteFromStartDay = joda.minuteOfDay
        return minuteFromStartDay.toFloat()
    }

    override fun onDateSet(picker: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val currentDate = DateTime(date.value!!)
        val clicked = DateTime
            .now()
            .withYear(year)
            .withMonthOfYear(month + 1)
            .withDayOfMonth(dayOfMonth)

        if (currentDate.withTimeAtStartOfDay() == clicked.withTimeAtStartOfDay()) {
            // Same date clicked nothing to change
            return
        }

        val newDate = DateTime(date.value)
            .withYear(clicked.year)
            .withMonthOfYear(clicked.monthOfYear)
            .withDayOfMonth(dayOfMonth)

        updateDate(newDate.toDate())
        updateDateTimesSet()
    }

    override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
        Timber.tag("XXXX").d("onStartTrackingTouch")
    }

    override fun onRangeChanged(
        view: RangeSeekBar,
        leftValue: Float,
        rightValue: Float,
        isFromUser: Boolean
    ) {
        val startTime = updateLabel(leftValue, true)
        val endTime = updateLabel(rightValue, false)
        if (!isFromUser) {
            updateDateTimesSet()
        }
    }

    private fun updateLabel(currentValue: Float, isLeft: Boolean): Date {
        var currentTime = startTime.value!!
            .toJoda()
            .withTimeAtStartOfDay()
            .plusMinutes(currentValue.toInt())
        if (currentTime.minuteOfDay == 24 * 60) currentTime =
            currentTime.minusMinutes(1)

        if (isLeft) {
            startTime.value = currentTime.toDate()
        } else {
            endTime.value = currentTime.toDate()
        }

        return currentTime.toDate()
    }

    override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
        Timber.tag("XXXX").d("onStopTrackingTouch")
    }

    override fun onDatePickerClick() {
        navigationTarget.value = NavigationTarget.DatePicker(
            date.value ?: throw IllegalArgumentException("Did't initialize dateTimeRange???")
        )
    }

    private fun updateDate(date: Date) {
        this.date.value = date
    }

    override fun onTimeRangeBtnClick() {
    }

    private fun getInitialDate(): Date {
        val now = DateTime.now()
        return now.toDate()
    }
}

fun Date.toJoda(): DateTime {
    return DateTime(this)
}
