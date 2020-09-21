package com.example.studita.domain.entity.exercise

import com.example.studita.domain.entity.UserIdTokenData
import com.google.gson.annotations.SerializedName

data class ExerciseReportRequestData(
    val userIdTokenData: UserIdTokenData?,
    val exerciseReportData: ExerciseReportData
)

data class ExerciseReportData(
    val userId: Int?,
    val exerciseNumber: Int,
    val bugTypes: List<ExerciseReportType>
)

enum class ExerciseReportType(val number: Int){
    EXERCISE_MISTAKE(1),
    CANT_UNDERSTAND(2),
    ANSWER_IS_CORRECT(3),
    ANSWER_IS_INCORRECT(4)
}