package com.studita.data.repository.datasource.exercises.result

import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStore
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl

class ExerciseResultDataStoreFactoryImpl(
    private val exerciseResultDataStoreImpl: ExerciseResultDataStoreImpl
) : ExerciseResultDataStoreFactory {

    override fun create() =
        exerciseResultDataStoreImpl
}

interface ExerciseResultDataStoreFactory {

    fun create(): ExerciseResultDataStore
}