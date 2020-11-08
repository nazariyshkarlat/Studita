package com.studita.domain.service

import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData

interface SyncExercisesReports {

    fun scheduleSendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData)

}