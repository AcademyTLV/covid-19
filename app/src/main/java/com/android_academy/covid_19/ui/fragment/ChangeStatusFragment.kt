package com.android_academy.covid_19.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.change_status_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeStatusFragment : BottomSheetDialogFragment() {
    private val mainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.change_status_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    fun initView() {
        changeStatusFragmentOkButton.isEnabled = false
        changeStatusRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            changeStatusFragmentOkButton.isEnabled = true
        }

        imageChangeStatusClose.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    fun initObservers() {
    }

    companion object {
        val TAG = "ChangeStatusFragment"
        fun newInstance() =
            ChangeStatusFragment().apply { isCancelable = false }
    }
}
