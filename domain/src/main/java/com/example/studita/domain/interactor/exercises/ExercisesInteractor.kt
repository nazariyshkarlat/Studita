package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.interactor.ExercisesCacheStatus
import com.example.studita.domain.interactor.ExercisesStatus

interface ExercisesInteractor {


    suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean,
        retryCount: Int = 60
    ): ExercisesStatus

    suspend fun downloadOfflineExercises(retryCount: Int = 30): ExercisesCacheStatus

}