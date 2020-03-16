package com.android_academy.covid_19.ui.fragment.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.UserLocationModel
import kotlinx.android.synthetic.main.fragment_users_location_list.view.*
import java.text.SimpleDateFormat
import java.util.Date

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class UsersLocationListRecyclerViewAdapter(private val locations: List<UserLocationModel>) :
    RecyclerView.Adapter<UsersLocationListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_users_location_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = locations[position]
        holder.mIdView.text =
            SimpleDateFormat.getDateTimeInstance().format(item.time?.let { Date(it) } ?: Date())
        holder.mContentView.text =
            "lat: ${item.lat} lon: ${item.lon}, speed: ${item.speed}, accuracy: ${item.accuracy}"

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = locations.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
