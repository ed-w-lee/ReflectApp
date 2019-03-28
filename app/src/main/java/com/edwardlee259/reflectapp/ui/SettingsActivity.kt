package com.edwardlee259.reflectapp.ui

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.edwardlee259.reflectapp.R
import java.text.ParseException

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        private lateinit var blockingStartTimeKey: String

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            if (key != null) {
                val pref = findPreference<Preference>(key)
                if (pref is TimeDialogPreference && key == blockingStartTimeKey) {
                    setBlockingStartTimeSummary(pref)
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_general, rootKey)

            blockingStartTimeKey = getString(R.string.pref_key_blocking_start_time)
            val pref = findPreference<Preference>(blockingStartTimeKey)
            if (pref is TimeDialogPreference) {
                setBlockingStartTimeSummary(pref)
            }
        }

        private fun setBlockingStartTimeSummary(pref: TimeDialogPreference) {
            val time = pref.getTime()
            var timeStr = """${time / 60}:${time % 60}"""
            val sdf = SimpleDateFormat("HH:mm")
            try {
                timeStr = DateFormat.getTimeFormat(this.context).format(sdf.parse(timeStr))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            pref.summary = "Starts at %s".format(timeStr)
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            var dialogFragment: DialogFragment? = null
            if (preference is TimeDialogPreference) {
                dialogFragment = TimePreferenceDialogCompat.newInstance(preference.key)
            }

            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0)
                if (this.fragmentManager != null) {
                    dialogFragment.show(
                        this.fragmentManager!!,
                        "android.support.v7.preference.PreferenceFragment.DIALOG"
                    )
                }
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }
}