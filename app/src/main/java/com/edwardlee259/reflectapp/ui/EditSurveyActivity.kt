package com.edwardlee259.reflectapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edwardlee259.reflectapp.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit_survey.*

class EditSurveyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_survey)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
