package com.example.studita.data.repository.datasource.complete_exercises

class CompleteExercisesDataStoreFactoryImpl(
    private val completeExercisesDataStoreImpl: CompleteExercisesDataStoreImpl
) : CompleteExercisesDataStoreFactory {

    override fun create() =
        completeExercisesDataStoreImpl
}

interface CompleteExercisesDataStoreFactory {

    fun create(): CompleteExercisesDataStoreImpl

}