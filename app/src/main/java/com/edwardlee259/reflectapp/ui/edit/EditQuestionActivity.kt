package com.edwardlee259.reflectapp.ui.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edwardlee259.reflectapp.R

class EditQuestionActivity : AppCompatActivity() {

    companion object {
        val EXTRA_REPLY = "com.edwardlee259.reflectapp.EDIT_QUESTION_REPLY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

    }
}
