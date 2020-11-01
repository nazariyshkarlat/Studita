package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.repository.datasource.interesting.result.InterestingResultDataStore
import com.example.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl

class ExerciseResultDataStoreFactoryImpl(
    private val exerciseResultDataStoreImpl: ExerciseResultDataStoreImpl
) : ExerciseResultDataStoreFactory {

    override fun create() =
        exerciseResultDataStoreImpl
}

interface ExerciseResultDataStoreFactory {

    fun create(): ExerciseResultDataStore
}