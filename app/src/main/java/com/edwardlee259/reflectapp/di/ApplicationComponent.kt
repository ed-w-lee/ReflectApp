package com.edwardlee259.reflectapp.di

import com.edwardlee259.reflectapp.ui.edit.EditSurveyActivity
import com.edwardlee259.reflectapp.ui.fill.SurveyActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class])
interface ApplicationComponent {
    fun inject(editSurveyActivity: EditSurveyActivity)
    fun inject(surveyActivity: SurveyActivity)
}