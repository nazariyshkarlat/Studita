package com.studita.domain.entity.exercise

import com.studita.domain.entity.UserIdTokenData
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseReportRequestData(
    val userIdTokenData: UserIdTokenData?,
    val exerciseReportData: ExerciseReportData
)

@Serializable
data class ExerciseReportData(
    val exerciseNumber: Int,
    val bugTypes: List<ExerciseReportType>
)

enum class ExerciseReportType(val number: Int){
    EXERCISE_MISTAKE(1),
    CANT_UNDERSTAND(2),
    ANSWER_IS_CORRECT(3),
    ANSWER_IS_INCORRECT(4)
}