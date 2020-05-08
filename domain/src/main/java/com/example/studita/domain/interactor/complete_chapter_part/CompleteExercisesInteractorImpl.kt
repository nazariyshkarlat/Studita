package com.example.studita.domain.interactor.complete_chapter_part

import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.CompleteExercisesStatus
import com.example.studita.domain.repository.CompleteExercisesRepository
import com.example.studita.domain.service.SyncCompletedExercises

class CompleteExercisesInteractorImpl(private val repository: CompleteExercisesRepository, private val syncCompletedExercises: SyncCompletedExercises) : CompleteExercisesInteractor{
    override suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData) =
        if(completeExercisesRequestData.userIdToken != null) {
            try {
                when (repository.completeExercises(completeExercisesRequestData)) {
                    200 -> CompleteExercisesStatus.Success
                    else -> CompleteExercisesStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    syncCompletedExercises.scheduleCompleteExercises(completeExercisesRequestData.completedExercisesEntity)
                    CompleteExercisesStatus.Success
                } else
                    CompleteExercisesStatus.ServiceUnavailable
            }
        }else
            CompleteExercisesStatus.Success

}