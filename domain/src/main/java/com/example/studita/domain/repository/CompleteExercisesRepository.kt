package com.example.studita.domain.repository

import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.CompletedExercisesData
import java.util.*

interface CompleteExercisesRepository {

    suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData): Int

    suspend fun getLocalCompletedExercises(): List<CompletedExercisesData>?

    suspend fun deleteLocalCompletedExercises(dateTimeCompleted: Date)

    suspend fun clearLocalCompletedExercises()

    suspend fun addLocalCompletedExercises(completedExercisesData: CompletedExercisesData)
}