package com.studita.domain.repository

import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import java.util.*

interface CompleteExercisesRepository {

    suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData): Int

    suspend fun getLocalCompletedExercises(): List<CompletedExercisesData>?

    suspend fun deleteLocalCompletedExercises(dateTimeCompleted: Date)

    suspend fun clearLocalCompletedExercises()

    suspend fun addLocalCompletedExercises(completedExercisesData: CompletedExercisesData)
}