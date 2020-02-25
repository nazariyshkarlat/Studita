package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.repository.datasource.exercises.ExercisesDataStore

class ExerciseResultDataStoreFactoryImpl(
    private val exerciseResultDataStore: ExerciseResultDataStore
) : ExerciseResultDataStoreFactory {

    override fun create(priority: ExerciseResultDataStoreFactory.Priority) =
        if (priority == ExerciseResultDataStoreFactory.Priority.CLOUD)
            exerciseResultDataStore
        else
            exerciseResultDataStore
}

interface ExerciseResultDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): ExerciseResultDataStore
}