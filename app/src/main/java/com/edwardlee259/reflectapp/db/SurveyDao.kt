package com.edwardlee259.reflectapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.edwardlee259.reflectapp.vo.SurveyQuestion
import com.edwardlee259.reflectapp.vo.SurveyResponse

@Dao
abstract class SurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertQuestion(vararg surveyQuestions: SurveyQuestion)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateQuestion(surveyQuestion: SurveyQuestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSurveyQuestions(surveyQuestions: List<SurveyQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSurveyResponses(responses: List<SurveyResponse>)

    @Query("SELECT * FROM survey_questions ORDER BY `order` ASC")
    abstract fun loadAllQuestions(): LiveData<List<SurveyQuestion>>

    @Query("SELECT * FROM survey_responses WHERE question_id = :questionId")
    abstract fun loadResponses(questionId: String): LiveData<List<SurveyResponse>>

    @Query("DELETE FROM survey_questions WHERE uuid=:questionId")
    abstract fun deleteQuestion(vararg questionId: String)

}