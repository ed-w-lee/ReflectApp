package com.edwardlee259.reflectapp.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "survey_questions"
)
data class SurveyQuestion(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    @field:SerializedName("created")
    val created: Long = System.currentTimeMillis(),
    @field:SerializedName("modified")
    val modified: Long = System.currentTimeMillis(),
    @field:SerializedName("required")
    val required: Boolean,
    @field:SerializedName("question")
    val question: String,
    @field:SerializedName("questionType")
    val questionType: QuestionType,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("order")
    val order: Long
) {
    enum class QuestionType {
        NUMERIC,
        RATING,
        TEXT
    }
}