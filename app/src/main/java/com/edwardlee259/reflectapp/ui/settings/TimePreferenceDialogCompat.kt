package com.edwardlee259.reflectapp.ui.settings

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.edwardlee259.reflectapp.R

class TimePreferenceDialogCompat : PreferenceDialogFragmentCompat() {

    private lateinit var mTimePicker: TimePicker

    companion object {
        fun newInstance(key: String): TimePreferenceDialogCompat {
            val fragment = TimePreferenceDialogCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Throws(java.lang.IllegalStateException::class)
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        mTimePicker =
            view?.findViewById(R.id.time_picker) ?: throw IllegalStateException("No time picker found")
        val preference = preference

        if (preference is TimeDialogPreference) {
            val minutesAfterMidnight = preference.getTime()

            mTimePicker.setIs24HourView(DateFormat.is24HourFormat(this.context))
            mTimePicker.hour = minutesAfterMidnight / 60
            mTimePicker.minute = minutesAfterMidnight % 60
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val minutesAfterMidnight = (mTimePicker.hour * 60) + mTimePicker.minute

            val preference = preference

            if (preference is TimeDialogPreference) {
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    preference.setTime(minutesAfterMidnight)
                }
            }
        }
    }
}