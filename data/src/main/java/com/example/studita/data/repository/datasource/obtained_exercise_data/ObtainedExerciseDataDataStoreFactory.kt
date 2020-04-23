package com.example.studita.data.repository.datasource.obtained_exercise_data

class ObtainedExerciseDataDataStoreFactoryImpl(
    private val cloudObtainedExerciseDataDataStore: CloudObtainedExerciseDataDataStore
) : ObtainedExerciseDataDataStoreFactory {

    override fun create() =
        cloudObtainedExerciseDataDataStore
}

interface ObtainedExerciseDataDataStoreFactory{

    fun  create(): ObtainedExerciseDataDataStore
}