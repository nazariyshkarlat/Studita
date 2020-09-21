package com.example.studita.domain.service

import com.example.studita.domain.entity.exercise.ExerciseReportData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData

interface SyncExercisesReports {

    fun scheduleSendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData)

}