package com.studita.data.repository.datasource.exercises.result

import com.studita.data.entity.CompleteExercisesRequest
import com.studita.data.entity.exercise.ExerciseReportEntity
import com.studita.data.entity.exercise.ExerciseReportRequest
import com.studita.data.entity.exercise.ExerciseRequestEntity
import com.studita.data.entity.exercise.ExerciseResponseEntity

interface ExerciseResultDataStore {

    suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestEntity: ExerciseRequestEntity
    ): Pair<Int, ExerciseResponseEntity>

    suspend fun sendExerciseReport(
        exerciseReportRequest: ExerciseReportRequest
    ) : Int

}