package com.example.studita.domain.interactor.complete_chapter_part

import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.interactor.CompleteExercisesStatus

interface CompleteExercisesInteractor {

    suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData, retryCount: Int=30) : CompleteExercisesStatus

}