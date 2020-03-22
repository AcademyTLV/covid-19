package com.android_academy.covid_19.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.FilterDataModel
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.android_academy.covid_19.ui.fragment.main.FiltersViewModel.NavigationTarget
import com.android_academy.covid_19.ui.fragment.main.FiltersViewModel.NavigationTarget.DatePicker
import com.android_academy.covid_19.util.setSafeOnClickListener
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.filters_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FiltersFragment : Fragment(R.layout.filters_fragment) {

    private val mainViewModel: FilterDataModel by sharedViewModel<MainViewModelImpl>()

    private val viewModel: FiltersViewModel by viewModel<FiltersViewModelImpl>()

    private val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.apply {
            date.observe(viewLifecycleOwner, Observer {
                it?.let {
                    datePickerBtn.text = formatDate(it)
                }
            })
            onDatesSet.distinctUntilChanged()
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        mainViewModel.onChangeFilterDate(it.first, it.second)
                    }
                })
            startTime.observe(viewLifecycleOwner, Observer {
                it?.let { time ->
                    rangeSeekBar.silently(viewModel) {
                        leftSeekBar.setIndicatorText(timeFormat.format(time))
                    }
                }
            })
            endTime.observe(viewLifecycleOwner, Observer {
                it?.let { time ->
                    rangeSeekBar.silently(viewModel) {
                        rightSeekBar.setIndicatorText(timeFormat.format(time))
                    }
                }
            })
            progress.observe(viewLifecycleOwner, Observer {
                it?.let { progress ->
                    rangeSeekBar.silently(viewModel) {
                        setProgress(progress.start, progress.end)
                    }
                }
            })
            navigationTarget.observe(viewLifecycleOwner, Observer {
                it?.let { onNavigationChanged(it) }
            })
        }
    }

    private fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    private fun onNavigationChanged(navigationTarget: NavigationTarget) {
        when (navigationTarget) {
            is DatePicker -> {
                showDatePicker(navigationTarget.date)
            }
        }
    }

    private fun showDatePicker(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dpd = android.app.DatePickerDialog(
            this.requireContext(),
            R.style.DatePickerTheme,
            viewModel,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }

    private fun initView() {

        datePickerBtn.setSafeOnClickListener {
            viewModel.onDatePickerClick()
        }

        timeRangeButton.setSafeOnClickListener {
            viewModel.onTimeRangeBtnClick()
        }

        todayBtn.setSafeOnClickListener {
            viewModel.initialize()
        }

        changeStatusBtn.setSafeOnClickListener {
            mainViewModel.onChangeStatusButtonClick()
        }

        crossLocationBtn.setSafeOnClickListener {
            mainViewModel.onLocationMatchButtonClick()
        }

        initTimeRangeStyling()
    }

    private fun initTimeRangeStyling() {
        rangeSeekBar.apply {
            setRange(0F, 24 * 60F - 1, 60F)
            progressHeight = 5
            progressColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            setOnRangeChangedListener(viewModel)
        }
    }
}

fun <T> RangeSeekBar.silently(callback: OnRangeChangedListener, block: RangeSeekBar.() -> T) {
    setOnRangeChangedListener(null)
    block.invoke(this)
    setOnRangeChangedListener(callback)
}
