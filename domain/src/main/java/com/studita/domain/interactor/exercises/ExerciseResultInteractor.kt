package com.studita.domain.interactor.exercises

import com.studita.domain.entity.exercise.ExerciseData
import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.domain.interactor.ExerciseReportStatus
import com.studita.domain.interactor.ExerciseResultStatus

interface ExerciseResultInteractor {

    suspend fun getExerciseResult(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData,
        offlineMode: Boolean,
        retryCount: Int = 3
    ): ExerciseResultStatus

    suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData,
        retryCount: Int = 3
    ) : ExerciseReportStatus

}