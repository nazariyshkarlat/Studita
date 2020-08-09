package com.example.studita.domain.service

import com.example.studita.domain.entity.CompleteExercisesRequestData

interface SyncCompletedExercises {

    fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData)

}