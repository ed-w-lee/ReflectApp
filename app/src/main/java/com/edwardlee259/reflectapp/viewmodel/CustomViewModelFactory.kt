package com.edwardlee259.reflectapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.edwardlee259.reflectapp.repository.SurveyRepository
import javax.inject.Inject

class CustomViewModelFactory @Inject constructor(repository: SurveyRepository) :
    ViewModelProvider.Factory {

    private val mRepository = repository

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SurveyQuestionViewModel::class.java) ->
                SurveyQuestionViewModel(mRepository) as T
            modelClass.isAssignableFrom(SurveyResponseViewModel::class.java) ->
                SurveyResponseViewModel(mRepository) as T
            else -> throw IllegalArgumentException("View Model Not Found")
        }
    }
}