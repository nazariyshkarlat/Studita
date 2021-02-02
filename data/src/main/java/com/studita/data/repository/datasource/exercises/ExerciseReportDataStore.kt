package com.studita.data.repository.datasource.exercises


import com.studita.data.entity.exercise.ExerciseReportRequest

interface ExerciseReportDataStore {

    suspend fun sendExerciseReport(
        exerciseReportRequest: ExerciseReportRequest
    ) : Int

}