package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.interactor.ExercisesCacheStatus

interface ExercisesInteractor {

    suspend fun getExercises(chapterPartNumber: Int, offlineMode: Boolean, retryCount: Int = 60) : ExercisesStatus

    suspend fun downloadOfflineExercises(): ExercisesCacheStatus

}