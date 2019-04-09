package com.edwardlee259.reflectapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.block.BlockingService
import com.edwardlee259.reflectapp.ui.edit.EditSurveyActivity
import com.edwardlee259.reflectapp.ui.fill.SurveyActivity
import com.edwardlee259.reflectapp.ui.settings.SettingsActivity
import com.edwardlee259.reflectapp.utils.UsageStatsPermissions
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mSharedPreferences: SharedPreferences
    private var mServiceRunning: Boolean = false
    private lateinit var mListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == getString(R.string.pref_key_service_status)) {
                    val serviceIsStopped = sharedPreferences.getBoolean(key, true)
                    setServiceToggle(!serviceIsStopped)
                }
            }

        service_toggle_btn.setOnClickListener {
            if (mServiceRunning) {
                val intent = Intent(this, BlockingService::class.java)
                stopService(intent)
            } else {
                val dialog = UsageStatsPermissions.requestPermissionsDialog(this)
                if (dialog != null) {
                    dialog.show()
                } else {
                    val intent = Intent(this, BlockingService::class.java)
                    startService(intent)
                }
            }
        }

        edit_survey.setOnClickListener {
            startActivity(Intent(this, EditSurveyActivity::class.java))
        }
        take_survey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
        view_responses.setOnClickListener {
            Toast.makeText(this, "Still need to implement", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val isServiceStopped = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(getString(R.string.pref_key_service_status), true)
        setServiceToggle(!isServiceStopped)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(mListener)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(mListener)
    }

    private fun setServiceToggle(isServiceRunning: Boolean) {
        if (isServiceRunning) {
            mServiceRunning = true
            service_toggle_btn.text = "Stop Service"
        } else {
            mServiceRunning = false
            service_toggle_btn.text = "Start Service"
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                gotoSettings()
                return false
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_settings -> {
                gotoSettings()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun gotoSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

}
