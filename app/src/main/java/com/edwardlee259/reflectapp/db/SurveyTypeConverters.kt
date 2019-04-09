package com.edwardlee259.reflectapp.db

import androidx.room.TypeConverter
import com.edwardlee259.reflectapp.vo.SurveyQuestion

object SurveyTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToQuestionType(data: String?): SurveyQuestion.QuestionType? {
        return if (data == null) null else SurveyQuestion.QuestionType.valueOf(data)
    }

    @TypeConverter
    @JvmStatic
    fun questionTypeToString(questionType: SurveyQuestion.QuestionType?): String? {
        return questionType?.name
    }
}