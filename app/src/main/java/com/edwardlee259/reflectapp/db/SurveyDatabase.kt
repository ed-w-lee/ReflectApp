package com.edwardlee259.reflectapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import com.edwardlee259.reflectapp.vo.SurveyResponse

@Database(
    entities = [SurveyQuestion::class, SurveyResponse::class],
    version = 1,
    exportSchema = false
)
abstract class SurveyDatabase : RoomDatabase() {

    abstract fun surveyQuestionDao(): SurveyDao

}