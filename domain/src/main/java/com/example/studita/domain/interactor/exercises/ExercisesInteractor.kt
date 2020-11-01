package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.interactor.ExercisesCacheStatus
import com.example.studita.domain.interactor.ExercisesStatus

interface ExercisesInteractor {


    suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean,
        retryCount: Int = Int.MAX_VALUE
    ): ExercisesStatus

    suspend fun downloadOfflineExercises(retryCount: Int = 3): ExercisesCacheStatus

}