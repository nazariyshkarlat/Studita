package com.studita.data.entity.exercise

import com.studita.data.entity.UserIdToken
import com.studita.data.entity.toRawEntity
import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseReportRequest(@SerialName("auth_data")val userIdToken: UserIdToken?,
                                 @SerialName("report_data") val exerciseReportEntity: ExerciseReportEntity)

@Serializable
data class ExerciseReportEntity(
    @SerialName("exercise_number") val exerciseNumber: Int,
    @SerialName("bugs") val bugs: List<Int>
)

fun ExerciseReportRequestData.toRawEntity() = ExerciseReportRequest(userIdTokenData?.toRawEntity(), exerciseReportData.toRawEntity())

fun ExerciseReportData.toRawEntity() = ExerciseReportEntity(exerciseNumber, bugTypes.map { it.number })