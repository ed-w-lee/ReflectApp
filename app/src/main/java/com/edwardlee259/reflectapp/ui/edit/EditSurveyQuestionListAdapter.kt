package com.edwardlee259.reflectapp.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.vo.SurveyQuestion

class EditSurveyQuestionListAdapter(context: Context) :
    RecyclerView.Adapter<EditSurveyQuestionListAdapter.EditSurveyQuestionViewHolder>() {

    private val mContext = context
    private val mInflater = LayoutInflater.from(context)
    private var mSurveyQuestions: List<SurveyQuestion>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditSurveyQuestionViewHolder {
        return EditSurveyQuestionViewHolder(
            mInflater.inflate(
                R.layout.rv_item_edit_survey,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mSurveyQuestions?.size ?: 0

    override fun onBindViewHolder(holder: EditSurveyQuestionViewHolder, position: Int) {
        val surveyQuestion: SurveyQuestion? = mSurveyQuestions?.get(position)
        if (surveyQuestion != null) {
            when (surveyQuestion.questionType) {
                SurveyQuestion.QuestionType.NUMERIC -> {
                    holder.typeIconView.setImageResource(
                        R.drawable.ic_numeric_black_24dp
                    )
                    holder.typeIconView.contentDescription = "numeric question"
                }
                SurveyQuestion.QuestionType.RATING -> {
                    holder.typeIconView.setImageResource(
                        R.drawable.ic_rating_black_24dp
                    )
                    holder.typeIconView.contentDescription = "rating question"
                }
                SurveyQuestion.QuestionType.TEXT -> {
                    holder.typeIconView.setImageResource(
                        R.drawable.ic_text_black_24dp
                    )
                    holder.typeIconView.contentDescription = "text question"
                }
            }
            holder.questionTextView.text = surveyQuestion.question
            holder.descriptionTextView.text = surveyQuestion.description
        } else {
            holder.questionTextView.text = "QUESTION OUT OF BOUNDS"
        }
    }

    fun setSurveyQuestions(questions: List<SurveyQuestion>?) {
        mSurveyQuestions = questions
        notifyDataSetChanged()
    }

    class EditSurveyQuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeIconView: ImageView = itemView.findViewById(R.id.type_icon)
        val questionTextView: TextView = itemView.findViewById(R.id.question_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
    }
}