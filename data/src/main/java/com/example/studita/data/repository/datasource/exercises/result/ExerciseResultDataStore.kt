package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.data.entity.exercise.ExerciseReportEntity
import com.example.studita.data.entity.exercise.ExerciseReportRequest
import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity

interface ExerciseResultDataStore {

    suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestEntity: ExerciseRequestEntity
    ): Pair<Int, ExerciseResponseEntity>

    suspend fun sendExerciseReport(
        exerciseReportRequest: ExerciseReportRequest
    ) : Int

}