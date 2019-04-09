package com.edwardlee259.reflectapp.di

import com.edwardlee259.reflectapp.db.SurveyDao
import com.edwardlee259.reflectapp.db.SurveyDatabase
import com.edwardlee259.reflectapp.repository.SurveyRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class])
interface ApplicationComponent {
    fun surveyDao(): SurveyDao
    fun surveyDatabase(): SurveyDatabase
    fun surveyRepository(): SurveyRepository
}