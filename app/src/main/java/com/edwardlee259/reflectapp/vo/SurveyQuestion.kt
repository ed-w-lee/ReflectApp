package com.edwardlee259.reflectapp.vo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.edwardlee259.reflectapp.db.SurveyTypeConverters
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "survey_questions",
    indices = [Index(value = ["order"], unique = true)]
)
@TypeConverters(SurveyTypeConverters::class)
data class SurveyQuestion(
    @PrimaryKey
    @field:SerializedName("uuid")
    val uuid: String = UUID.randomUUID().toString(),
    @field:SerializedName("created")
    val created: Long = System.currentTimeMillis(),
    @field:SerializedName("modified")
    var modified: Long = System.currentTimeMillis(),
    @field:SerializedName("required")
    var required: Boolean,
    @field:SerializedName("question")
    var question: String,
    @field:SerializedName("questionType")
    var questionType: QuestionType,
    @field:SerializedName("description")
    var description: String,
    @field:SerializedName("order")
    var order: Long = -1
) : Serializable {
    enum class QuestionType(private val readable: String) {
        NUMERIC("Numeric"),
        RATING("Rating"),
        TEXT("Text");

        override fun toString(): String {
            return readable
        }
    }
}