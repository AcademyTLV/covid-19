package com.android_academy.covid_19.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.apply {
            resultTextView.observe(viewLifecycleOwner, Observer {
                message.text = it
            })
        }
    }

    private fun initView() {
        btnLogin.setOnClickListener {
            viewModel.onLoginClick()
        }
    }

    companion object {
        fun newInstance() =
            MainFragment()
    }
}
