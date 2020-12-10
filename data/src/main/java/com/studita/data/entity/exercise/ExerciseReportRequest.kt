package com.studita.data.entity.exercise

import com.studita.data.entity.UserIdToken
import com.studita.data.entity.toRawEntity
import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.google.gson.annotations.SerializedName

data class ExerciseReportRequest(@SerializedName("auth_data")val userIdToken: UserIdToken?, @SerializedName("report_data") val exerciseReportEntity: ExerciseReportEntity)

data class ExerciseReportEntity(
    @SerializedName("exercise_number") val exerciseNumber: Int,
    @SerializedName("bugs") val bugs: List<Int>
)

fun ExerciseReportRequestData.toRawEntity() = ExerciseReportRequest(userIdTokenData?.toRawEntity(), exerciseReportData.toRawEntity())

fun ExerciseReportData.toRawEntity() = ExerciseReportEntity(exerciseNumber, bugTypes.map { it.number })