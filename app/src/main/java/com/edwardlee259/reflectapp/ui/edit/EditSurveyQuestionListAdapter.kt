package com.edwardlee259.reflectapp.ui.edit

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import java.util.*

class EditSurveyQuestionListAdapter(context: Activity, listener: OnItemClickListener? = null) :
    RecyclerView.Adapter<EditSurveyQuestionListAdapter.EditSurveyQuestionViewHolder>() {

    private val mContext = context
    private val mInflater = LayoutInflater.from(context)
    private var mSurveyQuestions: List<SurveyQuestion>? = null
    private val mListener: OnItemClickListener? = listener

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
            holder.requiredView.setTextColor(
                mContext.getColor(
                    when (surveyQuestion.required) {
                        true -> R.color.colorRequired
                        false -> R.color.colorOptional
                    }
                )
            )
            if (mListener != null) {
                holder.itemView.setOnClickListener {
                    mListener.onItemClick(surveyQuestion)
                }
            }
        } else {
            holder.questionTextView.text = "QUESTION OUT OF BOUNDS"
        }
    }

    fun setSurveyQuestions(questions: List<SurveyQuestion>?) {
        mSurveyQuestions = questions
        mSurveyQuestions?.sortedBy { it.order }
        notifyDataSetChanged()
    }

    fun onItemMove(fromPos: Int, toPos: Int) {
        Collections.swap(mSurveyQuestions, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    fun getQuestionAtPosition(position: Int): SurveyQuestion? {
        if (mSurveyQuestions != null && position >= 0 && position < mSurveyQuestions!!.size) {
            return mSurveyQuestions!![position]
        }
        return null
    }

    inner class EditSurveyQuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val typeIconView: ImageView = itemView.findViewById(R.id.type_icon)
        val questionTextView: TextView = itemView.findViewById(R.id.question_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        val requiredView: TextView = itemView.findViewById(R.id.required_icon)

        override fun onClick(v: View?) {
            val currentQuestion = mSurveyQuestions?.get(adapterPosition)
            if (currentQuestion != null) {
                val updateIntent = Intent(mContext, EditQuestionActivity::class.java)
                updateIntent.putExtra(
                    EditSurveyActivity.INTENT_QUESTION_KEY,
                    currentQuestion
                )
                mContext.startActivityForResult(
                    updateIntent,
                    EditSurveyActivity.UPDATE_QUESTION_ACTIVITY_REQUEST_CODE
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(question: SurveyQuestion)
    }
}