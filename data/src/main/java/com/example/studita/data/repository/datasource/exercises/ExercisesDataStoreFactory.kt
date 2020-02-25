package com.example.studita.data.repository.datasource.exercises

class ExercisesDataStoreFactoryImpl(
    private val exercisesDataStore: ExercisesDataStore
) : ExercisesDataStoreFactory {

    override fun create(priority: ExercisesDataStoreFactory.Priority) =
        if (priority == ExercisesDataStoreFactory.Priority.CLOUD)
            exercisesDataStore
        else
            exercisesDataStore
}

interface ExercisesDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): ExercisesDataStore
}