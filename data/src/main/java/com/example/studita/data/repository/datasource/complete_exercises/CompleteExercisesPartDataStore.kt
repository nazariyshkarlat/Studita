package com.example.studita.data.repository.datasource.complete_exercises

import com.example.studita.data.entity.CompleteExercisesRequest

interface CompleteExercisesPartDataStore {

    suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int

}