package com.edwardlee259.reflectapp.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.edwardlee259.reflectapp.db.SurveyDao
import com.edwardlee259.reflectapp.db.SurveyDatabase
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import com.edwardlee259.reflectapp.vo.SurveyResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRepository @Inject constructor(
    private val db: SurveyDatabase,
    private val surveyDao: SurveyDao
) {

    fun loadAllQuestions(): LiveData<List<SurveyQuestion>> = surveyDao.loadAllQuestions()

    fun loadResponsesFor(questionId: String): LiveData<List<SurveyResponse>> =
        surveyDao.loadResponses(questionId)

    fun insertQuestion(question: SurveyQuestion) {
        InsertQuestionAsyncTask(surveyDao).execute(question)
    }

    class InsertQuestionAsyncTask(dao: SurveyDao) : AsyncTask<SurveyQuestion, Void, Void>() {
        private val mDao = dao
        override fun doInBackground(vararg params: SurveyQuestion?): Void? {
            params[0]?.let { mDao.insert(it) }
            return null
        }
    }

    fun insertResponses(responses: List<SurveyResponse>) {
        InsertResponsesAsyncTask(surveyDao).execute(responses)
    }

    class InsertResponsesAsyncTask(dao: SurveyDao) : AsyncTask<List<SurveyResponse>, Void, Void>() {
        private val mDao = dao
        override fun doInBackground(vararg params: List<SurveyResponse>?): Void? {
            params[0]?.let { mDao.insertSurveyResponses(it) }
            return null
        }
    }

    fun deleteQuestion(questionId: String) {
        DeleteQuestionAsyncTask(surveyDao).execute(questionId)
    }

    class DeleteQuestionAsyncTask(dao: SurveyDao) : AsyncTask<String, Void, Void>() {
        private val mDao = dao
        override fun doInBackground(vararg params: String?): Void? {
            params[0]?.let { mDao.deleteQuestion(it) }
            return null
        }
    }
}