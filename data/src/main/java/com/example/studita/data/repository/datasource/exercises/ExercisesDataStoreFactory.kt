package com.example.studita.data.repository.datasource.exercises

class ExercisesDataStoreFactoryImpl(
    private val exercisesDataStoreImpl: ExercisesDataStoreImpl
) : ExercisesDataStoreFactory {

    override fun create() =
        exercisesDataStoreImpl
}

interface ExercisesDataStoreFactory {
    fun create(): ExercisesDataStore
}