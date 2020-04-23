package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.repository.datasource.exercises.ExercisesDataStore
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreImpl

class ExerciseResultDataStoreFactoryImpl(
    private val exerciseResultDataStoreImpl: ExerciseResultDataStoreImpl
) : ExerciseResultDataStoreFactory {

    override fun create() =
        exerciseResultDataStoreImpl
}

interface ExerciseResultDataStoreFactory {

    fun create(): ExerciseResultDataStore
}