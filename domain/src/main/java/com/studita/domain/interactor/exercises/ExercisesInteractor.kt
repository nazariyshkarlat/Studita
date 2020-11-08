package com.studita.domain.interactor.exercises

import com.studita.domain.interactor.ExercisesCacheStatus
import com.studita.domain.interactor.ExercisesStatus

interface ExercisesInteractor {


    suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean,
        retryCount: Int = Int.MAX_VALUE
    ): ExercisesStatus

    suspend fun downloadOfflineExercises(retryCount: Int = 3): ExercisesCacheStatus

}