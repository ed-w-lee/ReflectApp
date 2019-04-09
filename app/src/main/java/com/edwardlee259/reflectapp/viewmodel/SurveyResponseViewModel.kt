package com.edwardlee259.reflectapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.edwardlee259.reflectapp.repository.SurveyRepository
import com.edwardlee259.reflectapp.vo.SurveyResponse

class SurveyResponseViewModel(private val repository: SurveyRepository) : ViewModel() {

    fun loadResponsesFor(questionId: String): LiveData<List<SurveyResponse>> =
        repository.loadResponsesFor(questionId)

    fun insertResponses(responses: List<SurveyResponse>) = repository.insertResponses(responses)

}