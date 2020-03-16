package com.android_academy.covid_19.ui.fragment.intro

import androidx.room.TypeConverter
import com.android_academy.covid_19.R
import kotlinx.android.synthetic.main.intro_fragment.view.*
import java.util.Date

object Utils {
    val ID_TYPE_MAP = mapOf<Int, String>(
        R.id.positiveButton to "positive",
        R.id.notPositiveButton to "notPositive",
        R.id.wasPositiveButton to "wasPositive"
    )

    fun getTypeByButton(buttonId: Int): String? = ID_TYPE_MAP[buttonId]
}
