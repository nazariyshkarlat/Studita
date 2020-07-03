package com.example.studita.domain.service

import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.CompletedExercisesData

interface SyncCompletedExercises {

    fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData)

}