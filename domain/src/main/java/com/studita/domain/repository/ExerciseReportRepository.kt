package com.studita.domain.repository

import com.studita.domain.entity.exercise.*

interface ExerciseReportRepository {

    suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData
    ) : Int

}