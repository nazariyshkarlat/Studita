package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.interactor.ExerciseResultStatus

interface ExerciseResultInteractor {

    suspend fun getExerciseResult(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData,
        offlineMode: Boolean,
        retryCount: Int = 60
    ): ExerciseResultStatus

}