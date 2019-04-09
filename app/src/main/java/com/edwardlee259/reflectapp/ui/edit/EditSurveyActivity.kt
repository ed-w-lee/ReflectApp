package com.edwardlee259.reflectapp.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.ReflectApplication
import com.edwardlee259.reflectapp.viewmodel.SurveyQuestionViewModel
import kotlinx.android.synthetic.main.activity_edit_survey.*
import javax.inject.Inject

class EditSurveyActivity : AppCompatActivity() {

    private val NEW_QUESTION_ACTIVITY_REQUEST_CODE = 1
    private val UPDATE_QUESTION_ACTIVITY_REQUEST_CODE = 1

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var surveyQuestionViewModel: SurveyQuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_survey)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivityForResult(
                Intent(this, EditQuestionActivity::class.java),
                NEW_QUESTION_ACTIVITY_REQUEST_CODE
            )
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set up adapter
        val recyclerView: RecyclerView = findViewById(R.id.edit_survey_recycler_view)

        // inject dependencies in
        (this.application as ReflectApplication).getApplicationComponent().inject(this)

        // observe ViewModel
        surveyQuestionViewModel =
            ViewModelProviders.of(this).get(SurveyQuestionViewModel::class.java)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                NEW_QUESTION_ACTIVITY_REQUEST_CODE -> {

                }
                UPDATE_QUESTION_ACTIVITY_REQUEST_CODE -> {

                }
            }
        }
    }
}
