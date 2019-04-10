package com.edwardlee259.reflectapp.ui.fill

import android.app.Activity
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import com.edwardlee259.reflectapp.vo.SurveyResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SurveyQuestionListAdapter(context: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    private val mInflater = LayoutInflater.from(context)
    private var mSurveyQuestions: List<SurveyQuestion>? = null
    private var mResponses: Array<String>? = null
    private var mUnfilledList: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        // submit element
        return when (viewType) {
            SurveyQuestion.QuestionType.NUMERIC.num -> {
                SurveyTextViewHolder(
                    mInflater.inflate(
                        R.layout.survey_question_numeric,
                        parent,
                        false
                    ), EditTextListener()
                )
            }
            SurveyQuestion.QuestionType.TEXT.num -> {
                SurveyTextViewHolder(
                    mInflater.inflate(
                        R.layout.survey_question_text,
                        parent,
                        false
                    ), EditTextListener()
                )
            }
            SurveyQuestion.QuestionType.RATING.num -> {
                SurveyRatingViewHolder(
                    mInflater.inflate(
                        R.layout.survey_question_rating,
                        parent,
                        false
                    ), RatingListener()
                )
            }
            else -> {
                throw IllegalArgumentException("unknown view type: %d".format(viewType))
            }
        }
    }

    /**
     * Returns 0 if position is num survey questions,
     *         -1 if OOB, or
     *         view type corresponding to question type otherwise
     */
    override fun getItemViewType(position: Int): Int {
        return getQuestionAtPosition(position)?.questionType?.num ?: -1
    }

    override fun getItemCount(): Int = mSurveyQuestions?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val question = getQuestionAtPosition(position)
        when (viewType) {
            SurveyQuestion.QuestionType.NUMERIC.num,
            SurveyQuestion.QuestionType.TEXT.num -> {
                (holder as SurveyTextViewHolder).let {
                    it.listener.updatePosition(holder.adapterPosition)
                    it.fieldTextView.setText(mResponses?.get(holder.adapterPosition) ?: "")
                    it.questionTextView.text = Html.fromHtml(
                        Html.escapeHtml(
                            question?.question ?: ""
                        ) + "<font color=red>*</font>"
                    )
                    it.descriptionTextView.text = question?.description ?: ""
                    if (holder.adapterPosition in mUnfilledList) {
                        it.fieldLayout.isErrorEnabled = true
                        it.fieldLayout.error = "Response required"
                    }
                }
            }
            SurveyQuestion.QuestionType.RATING.num -> {
                (holder as SurveyRatingViewHolder).let {
                    it.listener.updatePosition(holder.adapterPosition)
                    it.ratingBarView.rating =
                        (mResponses?.get(holder.adapterPosition) ?: "0").toFloat()
                    it.questionTextView.text = question?.question ?: ""
                    it.descriptionTextView.text = question?.description ?: ""
                }
            }
            else -> {
                throw IllegalArgumentException("unknown view type: %d".format(viewType))
            }
        }
    }

    fun setSurveyQuestions(questions: List<SurveyQuestion>?) {
        mSurveyQuestions = questions
        mSurveyQuestions?.sortedBy { it.order }
        mResponses = Array(mSurveyQuestions?.size ?: 0) {
            when (mSurveyQuestions?.get(it)?.questionType) {
                SurveyQuestion.QuestionType.RATING -> "0"
                else -> ""
            }
        }
        mUnfilledList.clear()
        notifyDataSetChanged()
    }

    /**
     * Returns list of SurveyResponses if all required responses are filled,
     *         null otherwise
     */
    fun getSurveyResponses(): List<SurveyResponse>? {
        if (mResponses == null || mSurveyQuestions == null)
            return null
        val questions = mSurveyQuestions!!
        val responses = mResponses!!
        assert(responses.size == questions.size)
        val responseList = mutableListOf<SurveyResponse>()
        for (i in 0..(questions.size - 1)) {
            if (responses[i].isBlank()) {
                if (questions[i].required) {
                    mUnfilledList.add(i)
                }
            } else {
                responseList.add(
                    SurveyResponse(
                        questionId = questions[i].uuid,
                        response = responses[i]
                    )
                )
            }
        }
        return if (mUnfilledList.isEmpty()) responseList else null
    }

    fun getQuestionAtPosition(position: Int): SurveyQuestion? {
        if (mSurveyQuestions != null && position >= 0 && position < mSurveyQuestions!!.size) {
            return mSurveyQuestions!![position]
        }
        return null
    }

    inner class SurveyTextViewHolder(itemView: View, val listener: EditTextListener) :
        RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.question_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        val fieldTextView: TextInputEditText = itemView.findViewById(R.id.response_field)
        val fieldLayout: TextInputLayout = itemView.findViewById(R.id.response_layout)

        init {
            fieldTextView.addTextChangedListener(listener)
        }
    }

    inner class SurveyRatingViewHolder(itemView: View, val listener: RatingListener) :
        RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.question_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        val ratingBarView: RatingBar = itemView.findViewById(R.id.response_field)

        init {
            ratingBarView.onRatingBarChangeListener = listener
        }
    }

    inner class EditTextListener : TextWatcher {
        private var position: Int? = null

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (position != null) {
                mResponses?.set(position!!, s.toString())
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // no op
        }

    }

    inner class RatingListener : RatingBar.OnRatingBarChangeListener {
        private var position: Int? = null

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
            if (position != null) {
                mResponses?.set(position!!, rating.toString())
            }
        }

    }
}