package com.studita.domain.service

import com.studita.domain.entity.CompleteExercisesRequestData

interface SyncCompletedExercises {

    fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData)

}