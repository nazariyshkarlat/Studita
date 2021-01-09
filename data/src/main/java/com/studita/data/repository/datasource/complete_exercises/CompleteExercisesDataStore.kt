package com.studita.data.repository.datasource.complete_exercises

import com.studita.data.entity.CompleteExercisesRequest
import com.studita.data.entity.CompletedExercisesEntity

interface CompleteExercisesDataStore {

    suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int

    suspend fun getLocalCompletedExercises(): List<CompletedExercisesEntity>?

    suspend fun deleteLocalCompletedExercises(dateTimeCompleted: String)

    suspend fun clearLocalCompletedExercises()

    suspend fun addLocalCompletedExercises(completedExercisesEntity: CompletedExercisesEntity)
}