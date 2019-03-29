package com.edwardlee259.reflectapp.ui

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.edwardlee259.reflectapp.R
import java.text.ParseException

class SettingsActivity : AppCompatActivity() {
    companion object {
        private val LOG_TAG = SettingsActivity::class.java.simpleName
    }

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
        private lateinit var serviceStatusKey: String

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            if (key != null) {
                val pref = findPreference<Preference>(key)
                Log.d(LOG_TAG, "preference with key: $key updated")
                if (pref is TimeDialogPreference && key == blockingStartTimeKey) {
                    setBlockingStartTimeSummary(pref)
                } else if (pref != null && key == serviceStatusKey) {
                    setServiceStatusPreference()
                }
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_general, rootKey)

            // set correct summary for start time
            blockingStartTimeKey = getString(R.string.pref_key_blocking_start_time)
            val pref = findPreference<Preference>(blockingStartTimeKey)
            if (pref is TimeDialogPreference) {
                setBlockingStartTimeSummary(pref)
            }

            // set summary for service status
            serviceStatusKey = getString(R.string.pref_key_service_status)
            setServiceStatusPreference()
        }

        private fun setBlockingStartTimeSummary(pref: TimeDialogPreference) {
            val time = pref.getTime()
            var timeStr = """${time / 60}:${time % 60}"""
            val sdf = SimpleDateFormat("HH:mm")
            try {
                timeStr = DateFormat.getTimeFormat(this.context).format(sdf.parse(timeStr))
            } catch (e: ParseException) {
                Log.e(LOG_TAG, "parse exception: ${e.message}")
            }

            pref.summary = "Starts at %s".format(timeStr)
            Log.d(LOG_TAG, "updated summary to ${pref.summary}")
        }

        private fun setServiceStatusPreference() {
            val status = preferenceManager.sharedPreferences.getBoolean(serviceStatusKey, true)
            val preference = findPreference<Preference>(serviceStatusKey)
            preference?.summary = if (status) {
                findPreference<Preference>(getString(R.string.pref_key_blocking))?.isEnabled = true
                "Stopped, settings are enabled."
            } else {
                findPreference<Preference>(getString(R.string.pref_key_blocking))?.isEnabled = false
                "Running, stop service to modify settings"
            }
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