package com.android_academy.covid_19.ui.fragment.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.FilterDataModel
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.android_academy.covid_19.util.setSafeOnClickListener
import kotlinx.android.synthetic.main.filters_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FiltersFragment : Fragment(R.layout.filters_fragment) {

    private val viewModel: FiltersViewModel by viewModel<FiltersViewModelImpl>()

    private val mainFilterViewModel: FilterDataModel by sharedViewModel<MainViewModelImpl>()

    val fullDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val shortDateFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())

    companion object {
        fun newInstance() =
            FiltersFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.startTimeLiveData.observe(viewLifecycleOwner, Observer {
            start_time.text = it
        })
        viewModel.endTimeLiveData.observe(viewLifecycleOwner, Observer {
            end_time.text = it
        })
        viewModel.filterDatesSet.observe(viewLifecycleOwner, Observer {
            it?.let {
                mainFilterViewModel.setDateTimeFilter(it.startDate, it.endDate)
            }
        })

        viewModel.dateLiveData.observe(viewLifecycleOwner, Observer {
            detailed_date.text = fullDateFormat.format(it)
            pick_date.text = shortDateFormat.format(it)
        })
    }

    private fun initView() {

        var calendar = Calendar.getInstance()

        date.setOnClickListener {
            openDatePicker()
        }

        detailed_date.setOnClickListener { openDatePicker() }

        start_time.setOnClickListener {
            openTimePicker(true)
        }

        end_time.setOnClickListener {
            openTimePicker(false)
        }

        today.setOnClickListener {
            viewModel.initialize()
        }

        change_status.setOnClickListener {
            // TODO: add open bottom sheet to change status
        }

        crossLocationButton.setSafeOnClickListener {
            mainFilterViewModel.onLocationMatchButtonClick()
        }

        // emergency_call.setOnClickListener {
        //     val intent = Intent(Intent.ACTION_DIAL)
        //     intent.data = Uri.parse("tel:101")
        //     activity?.startActivity(intent)
        // }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val dateValue = viewModel.getDate()
        if (dateValue != null) {
            calendar.time = dateValue
        }

        val dpd = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog
                .OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                viewModel.setDate(calendar.time)
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }

    private fun openTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        if (isStartTime) {
            calendar.time = viewModel.getTimeStart()
        } else {
            calendar.time = viewModel.getTimeEnd()
        }

        val timePickerDialog = TimePickerDialog(
            this.requireContext(),
            OnTimeSetListener { view, hour, minute ->
                if (isStartTime) {
                    viewModel.setStartTime(hour, minute)
                } else {
                    viewModel.setEndTime(hour, minute)
                }
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            true
        )
        timePickerDialog.updateTime(calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE])

        timePickerDialog.show()
    }
}
