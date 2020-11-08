package com.studita.domain.repository

import com.studita.domain.entity.exercise.*

interface ExerciseResultRepository {

    suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestData: ExerciseRequestData
    ): Pair<Int, ExerciseResponseData>

    suspend fun formExerciseResponse(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData
    ): ExerciseResponseData

    suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData
    ) : Int

}