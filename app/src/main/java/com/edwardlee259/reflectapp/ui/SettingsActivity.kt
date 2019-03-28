package com.edwardlee259.reflectapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.edwardlee259.reflectapp.R

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

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key != null) {
                val pref = findPreference<Preference>(key)
                if (pref is TimeDialogPreference && key == "blocking_start_time") {
                    val time = pref.getTime()
                    val hours = when (time / 60) {
                        0 -> 12
                        else -> time / 60
                    }.toString()
                    val minutes = (time % 60).toString()
                    pref.summary = "Starts at %s:%s".format(hours.padStart(2, '0'), minutes.padStart(2, '0'))
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_general, rootKey)
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