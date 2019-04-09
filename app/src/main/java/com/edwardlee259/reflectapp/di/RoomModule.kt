package com.edwardlee259.reflectapp.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.edwardlee259.reflectapp.db.SurveyDao
import com.edwardlee259.reflectapp.db.SurveyDatabase
import com.edwardlee259.reflectapp.repository.SurveyRepository
import com.edwardlee259.reflectapp.viewmodel.CustomViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {
    private val database =
        Room.databaseBuilder(application, SurveyDatabase::class.java, "Survey.db").build()

    @Provides
    @Singleton
    fun provideSurveyDatabase(application: Application): SurveyDatabase = database

    @Provides
    @Singleton
    fun provideSurveyDao(database: SurveyDatabase): SurveyDao = database.surveyQuestionDao()

    @Provides
    @Singleton
    fun provideSurveyRepository(surveyDao: SurveyDao): SurveyRepository =
        SurveyRepository(database, surveyDao)


    @Provides
    @Singleton
    fun provideViewModelFactory(repository: SurveyRepository): ViewModelProvider.Factory =
        CustomViewModelFactory(repository)

}