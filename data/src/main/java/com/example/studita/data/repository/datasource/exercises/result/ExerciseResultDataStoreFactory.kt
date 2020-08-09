package com.example.studita.data.repository.datasource.exercises.result

class ExerciseResultDataStoreFactoryImpl(
    private val exerciseResultDataStoreImpl: ExerciseResultDataStoreImpl
) : ExerciseResultDataStoreFactory {

    override fun create() =
        exerciseResultDataStoreImpl
}

interface ExerciseResultDataStoreFactory {

    fun create(): ExerciseResultDataStore
}