package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.*
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactory
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.repository.CompleteExercisesRepository

class CompleteExercisesRepositoryImpl(private val completeExercisesDataStoreFactory: CompleteExercisesDataStoreFactory) : CompleteExercisesRepository{
    override suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData): Int =
        completeExercisesDataStoreFactory.create().completeExercises(completeExercisesRequestData.toRawEntity())

}