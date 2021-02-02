package com.studita.data.repository.exercise

import com.studita.data.entity.exercise.toRawEntity
import com.studita.data.repository.datasource.exercises.ExerciseReportDataStoreFactory
import com.studita.domain.entity.exercise.*
import com.studita.domain.repository.ExerciseReportRepository

class ExerciseReportRepositoryImpl(private val exerciseReportDataStoreFactory: ExerciseReportDataStoreFactory) :
    ExerciseReportRepository {

    override suspend fun sendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData): Int {
        return exerciseReportDataStoreFactory.create().sendExerciseReport(exerciseReportRequestData.toRawEntity())
    }

}