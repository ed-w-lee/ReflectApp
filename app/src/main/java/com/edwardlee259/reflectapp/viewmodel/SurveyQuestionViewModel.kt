package com.edwardlee259.reflectapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.edwardlee259.reflectapp.repository.SurveyRepository
import com.edwardlee259.reflectapp.vo.SurveyQuestion

class SurveyQuestionViewModel(private val repository: SurveyRepository) : ViewModel() {

    fun getAllQuestions(): LiveData<List<SurveyQuestion>> = repository.loadAllQuestions()

    fun insertQuestion(question: SurveyQuestion) = repository.insertQuestion(question)

    fun updateQuestion(question: SurveyQuestion) = repository.updateQuestion(question)

    fun deleteQuestion(questionId: String) = repository.deleteQuestion(questionId)

}