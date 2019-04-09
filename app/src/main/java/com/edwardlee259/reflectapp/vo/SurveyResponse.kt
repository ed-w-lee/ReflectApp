package com.edwardlee259.reflectapp.vo

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "survey_responses",
    foreignKeys = [ForeignKey(
        entity = SurveyQuestion::class,
        parentColumns = ["uuid"],
        childColumns = ["question_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["question_id"])]
)
data class SurveyResponse(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),
    @field:SerializedName("created")
    val created: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "question_id")
    @field:SerializedName("question_id")
    val questionId: String,
    @field:SerializedName("response")
    val response: String
)