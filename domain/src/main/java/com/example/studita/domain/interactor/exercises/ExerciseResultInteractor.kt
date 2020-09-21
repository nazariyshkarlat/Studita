package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseReportData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.interactor.ExerciseReportStatus
import com.example.studita.domain.interactor.ExerciseResultStatus

interface ExerciseResultInteractor {

    suspend fun getExerciseResult(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData,
        offlineMode: Boolean,
        retryCount: Int = 60
    ): ExerciseResultStatus

    suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData,
        retryCount: Int = 60
    ) : ExerciseReportStatus

}