package com.android_academy.covid_19.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.CollisionLocationModel
import com.android_academy.covid_19.ui.activity.MainViewModelImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.collision_item.view.*
import kotlinx.android.synthetic.main.infection_match_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat

class InfectionMatchFragment : BottomSheetDialogFragment() {
    private val mainViewModel by sharedViewModel<MainViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.infection_match_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.collisionLocations.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                no_match_screen.visibility = View.GONE
                collisions_container.visibility = View.VISIBLE
                collisions_container.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                collisions_container.adapter = CollisionAdapter(it)
            } else {
                collisions_container.visibility = View.GONE
                no_match_screen.visibility = View.VISIBLE
            }
        })
    }

    companion object {
        const val TAG = "InfectionMatchFragment"
        fun newInstance() =
            InfectionMatchFragment().apply { isCancelable = true }
    }
}

class CollisionAdapter(private val collisionLocations: List<CollisionLocationModel>) :
    RecyclerView.Adapter<CollisionAdapter.CollisionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollisionAdapter.CollisionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.collision_item, parent, false)
        return CollisionViewHolder(view)
    }

    override fun getItemCount(): Int = collisionLocations.size

    override fun onBindViewHolder(holder: CollisionAdapter.CollisionViewHolder, position: Int) {
        holder.page.text = "${position + 1}/$itemCount"

        collisionLocations[position].also {
            holder.locationName.text = it.infected_name
            val infectedStartTime = SimpleDateFormat.getTimeInstance().format(it.infected_startTime)
            val infectedEndTime = SimpleDateFormat.getTimeInstance().format(it.infected_endTime)
            holder.locationTime.text = "$infectedStartTime - $infectedEndTime"
            if (position == 0)
                holder.previous.visibility = View.GONE
            else
                holder.previous.visibility = View.VISIBLE

            if (position == itemCount)
                holder.next.visibility = View.GONE
            else
                holder.next.visibility = View.VISIBLE
        }
    }

    inner class CollisionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val page: AppCompatTextView = view.page
        val locationName: AppCompatTextView = view.location_name
        val locationTime: AppCompatTextView = view.location_time
        val previous: ImageView = view.arrow_left
        val next: ImageView = view.arrow_right
    }
}
