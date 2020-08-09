package com.example.studita.domain.repository

import com.example.studita.domain.entity.CompleteExercisesRequestData

interface CompleteExercisesRepository {

    suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData): Int

}