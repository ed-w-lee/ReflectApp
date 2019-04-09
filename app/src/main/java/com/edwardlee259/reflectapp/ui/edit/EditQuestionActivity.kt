package com.edwardlee259.reflectapp.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import kotlinx.android.synthetic.main.activity_edit_question.*

class EditQuestionActivity : AppCompatActivity() {

    companion object {
        val EXTRA_REPLY = "com.edwardlee259.reflectapp.EDIT_QUESTION_REPLY"
    }

    private val SAVED_REQUIRED_KEY = "required"
    private val SAVED_QUESTION_KEY = "question"
    private val SAVED_DESCRIPTION_KEY = "description"
    private val SAVED_TYPE_KEY = "type"
    private var questionToReturn: SurveyQuestion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

        question_finish_btn.setOnClickListener {
            if (question_text.text.isEmpty()) {
                Toast.makeText(this, "Question cannot be empty", Toast.LENGTH_SHORT).show()
            }

            val replyIntent = Intent()
            if (questionToReturn == null) {
                questionToReturn = SurveyQuestion(
                    required = required_toggle.isChecked,
                    question = question_text.text.toString(),
                    description = description_text.text.toString(),
                    questionType = question_type_spinner.selectedItem as SurveyQuestion.QuestionType
                )
            } else {
                questionToReturn!!.required = required_toggle.isChecked
                questionToReturn!!.question = question_text.text.toString()
                questionToReturn!!.description = description_text.text.toString()
                questionToReturn!!.questionType =
                    question_type_spinner.selectedItem as SurveyQuestion.QuestionType
            }
            replyIntent.putExtra(EXTRA_REPLY, questionToReturn)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
        question_cancel_btn.setOnClickListener {
            val replyIntent = Intent()
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        }

        val adapter = ArrayAdapter<SurveyQuestion.QuestionType>(
            this,
            android.R.layout.simple_spinner_item,
            SurveyQuestion.QuestionType.values()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            question_type_spinner.adapter = it
        }

        if (savedInstanceState != null) {
            question_text.setText(savedInstanceState.getString(SAVED_QUESTION_KEY, ""))
            description_text.setText(savedInstanceState.getString(SAVED_DESCRIPTION_KEY, ""))
            savedInstanceState.getSerializable(SAVED_TYPE_KEY)?.also {
                question_type_spinner.setSelection(adapter.getPosition(it as SurveyQuestion.QuestionType))
            }
            required_toggle.isChecked = savedInstanceState.getBoolean(SAVED_REQUIRED_KEY, true)
        } else {
            questionToReturn =
                this.intent.getSerializableExtra(EditSurveyActivity.INTENT_QUESTION_KEY) as SurveyQuestion?
            if (questionToReturn != null) {
                question_text.setText(questionToReturn!!.question)
                description_text.setText(questionToReturn!!.description)
                question_type_spinner.setSelection(adapter.getPosition(questionToReturn!!.questionType))
                required_toggle.isChecked = questionToReturn!!.required
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (question_text.text.isNotEmpty()) {
            outState.putString(SAVED_QUESTION_KEY, question_text.text.toString())
        }
        if (description_text.text.isNotEmpty()) {
            outState.putString(SAVED_DESCRIPTION_KEY, description_text.text.toString())
        }
        outState.putSerializable(
            SAVED_TYPE_KEY,
            question_type_spinner.selectedItem as SurveyQuestion.QuestionType
        )
        outState.putBoolean(SAVED_REQUIRED_KEY, required_toggle.isChecked)
    }
}
