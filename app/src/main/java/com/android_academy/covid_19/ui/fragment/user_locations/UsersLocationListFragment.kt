package com.android_academy.covid_19.ui.fragment.user_locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.UserLocationModel
import com.android_academy.covid_19.repository.IUsersLocationRepo
import kotlinx.android.synthetic.main.fragment_users_location_list_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsersLocationListFragment : Fragment() {
    private val viewModel: UsersLocationViewModel by viewModel<UsersLocationListViewModelImpl>()

    private val usersLocationRepo: IUsersLocationRepo by inject()
    private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users_location_list_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserLocations(viewLifecycleOwner) { locations: List<UserLocationModel> ->
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    UsersLocationListRecyclerViewAdapter(
                        locations
                    )
            }
        }
    }
}
