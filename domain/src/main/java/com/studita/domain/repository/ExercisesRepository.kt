package com.studita.domain.repository

import com.studita.domain.entity.exercise.ExercisesResponseData

interface ExercisesRepository {

    suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean
    ): Pair<Int, ExercisesResponseData>

    suspend fun downloadOfflineExercises(): Int

}