package com.edwardlee259.reflectapp.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.ReflectApplication
import com.edwardlee259.reflectapp.viewmodel.SurveyQuestionViewModel
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import kotlinx.android.synthetic.main.activity_edit_survey.*
import javax.inject.Inject


class EditSurveyActivity : AppCompatActivity() {

    companion object {
        const val NEW_QUESTION_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_QUESTION_ACTIVITY_REQUEST_CODE = 2
        const val INTENT_QUESTION_KEY = "question_to_update"
    }

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
        val adapter = EditSurveyQuestionListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // inject dependencies in
        (this.application as ReflectApplication).getApplicationComponent().inject(this)

        // observe ViewModel
        surveyQuestionViewModel =
            ViewModelProviders.of(this).get(SurveyQuestionViewModel::class.java)
        surveyQuestionViewModel.getAllQuestions()
            .observe(this, Observer { questions: List<SurveyQuestion> ->
                adapter.setSurveyQuestions(questions)
            })

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT
        ) {
            var dragFrom: Int = -1
            var dragTo: Int = -1

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                dragFrom = fromPos
                dragTo = toPos
                adapter.onItemMove(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val questionToDelete = adapter.getQuestionAtPosition(viewHolder.adapterPosition)
                if (questionToDelete != null) {
                    val builder = AlertDialog.Builder(this@EditSurveyActivity)
                    builder.setMessage("Deleting a question will delete all stored responses. Is that ok?")
                        .setPositiveButton("OK") { dialog, _ ->
                            // Your action
                            surveyQuestionViewModel.deleteQuestion(questionToDelete.uuid)
                            dialog.dismiss()
                        }
                        .setNegativeButton("CANCEL") { dialog, _ ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.cancel()
                        }
                        .create().show()
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    val questionToUpdate = adapter.getQuestionAtPosition(dragFrom)
                    if (questionToUpdate != null) {
                        val orderBefore =
                            adapter.getQuestionAtPosition(dragTo - 1)?.order ?: Long.MIN_VALUE
                        val orderAfter =
                            adapter.getQuestionAtPosition(dragTo + 1)?.order ?: Long.MAX_VALUE
                        questionToUpdate.order = (orderBefore + orderAfter) / 2
                        surveyQuestionViewModel.updateQuestion(questionToUpdate)
                    }
                }
                dragFrom = -1
                dragTo = -1
            }
        })
        helper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                NEW_QUESTION_ACTIVITY_REQUEST_CODE -> {
                    val question =
                        data.getSerializableExtra(EditQuestionActivity.EXTRA_REPLY) as SurveyQuestion
                    surveyQuestionViewModel.insertQuestion(question)
                }
                UPDATE_QUESTION_ACTIVITY_REQUEST_CODE -> {
                    val question =
                        data.getSerializableExtra(EditQuestionActivity.EXTRA_REPLY) as SurveyQuestion
                    surveyQuestionViewModel.updateQuestion(question)
                }
                else -> {
                    Toast.makeText(this.applicationContext, "Unknown reply", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this.applicationContext, "Survey Question Not Saved", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
