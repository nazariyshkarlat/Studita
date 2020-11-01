package com.example.studita.data.repository.datasource.complete_exercises

import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.data.entity.CompletedExercisesEntity

interface CompleteExercisesPartDataStore {

    suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int

    suspend fun getLocalCompletedExercises(): List<CompletedExercisesEntity>?

    suspend fun deleteLocalCompletedExercises(dateTimeCompleted: String)

    suspend fun clearLocalCompletedExercises()

    suspend fun addLocalCompletedExercises(completedExercisesEntity: CompletedExercisesEntity)
}