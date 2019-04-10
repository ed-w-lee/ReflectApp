package com.edwardlee259.reflectapp.ui.fill

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.ReflectApplication
import com.edwardlee259.reflectapp.block.BlockingService
import com.edwardlee259.reflectapp.viewmodel.SurveyQuestionViewModel
import com.edwardlee259.reflectapp.viewmodel.SurveyResponseViewModel
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import kotlinx.android.synthetic.main.activity_survey.*
import javax.inject.Inject

class SurveyActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var surveyQuestionViewModel: SurveyQuestionViewModel
    lateinit var surveyResponseViewModel: SurveyResponseViewModel
    lateinit var mAdapter: SurveyQuestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        val recyclerView: RecyclerView = findViewById(R.id.survey_recycler_view)
        mAdapter = SurveyQuestionListAdapter(this)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // inject dependencies in
        (this.application as ReflectApplication).getApplicationComponent().inject(this)

        // get and observe ViewModels
        surveyResponseViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SurveyResponseViewModel::class.java)
        surveyQuestionViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SurveyQuestionViewModel::class.java)
        surveyQuestionViewModel.getAllQuestions()
            .observe(this, Observer { questions: List<SurveyQuestion> ->
                mAdapter.setSurveyQuestions(questions)
            })

        submit_btn.setOnClickListener {
            val responses = mAdapter.getSurveyResponses()
            if (responses == null) {
                Toast.makeText(
                    this,
                    "Required questions still require response",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                surveyResponseViewModel.insertResponses(responses)
                val intent = Intent(this, BlockingService::class.java)
                intent.action = BlockingService.ACTION_STOP_BLOCKING
                startService(intent)
                finish()
            }
        }
    }
}
