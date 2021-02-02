package com.studita.domain.interactor.exercises

import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.interactor.ExerciseReportStatus

interface ExerciseReportnteractor {

    suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData,
        retryCount: Int = 3
    ) : ExerciseReportStatus

}