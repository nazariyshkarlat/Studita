package com.studita.data.repository.datasource.exercises

class ExerciseReportDataStoreFactoryImpl(
    private val exerciseReportDataStoreImpl: ExerciseReportDataStoreImpl
) : ExerciseReportDataStoreFactory {

    override fun create() =
        exerciseReportDataStoreImpl
}

interface ExerciseReportDataStoreFactory {

    fun create(): ExerciseReportDataStore
}