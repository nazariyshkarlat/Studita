package com.example.studita.domain.interactor.complete_chapter_part

import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.CompleteExercisesStatus
import com.example.studita.domain.repository.CompleteExercisesRepository
import com.example.studita.domain.service.SyncCompletedExercises

class CompleteExercisesInteractorImpl(
    private val repository: CompleteExercisesRepository,
    private val syncCompletedExercises: SyncCompletedExercises
) : CompleteExercisesInteractor {
    override suspend fun completeExercises(
        completeExercisesRequestData: CompleteExercisesRequestData,
        retryCount: Int
    ): CompleteExercisesStatus =
        if (completeExercisesRequestData.userIdToken != null) {
            try {
                when (repository.completeExercises(completeExercisesRequestData)) {
                    200 -> CompleteExercisesStatus.Success
                    else -> CompleteExercisesStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException || e is ServerUnavailableException) {
                    when {
                        e is NetworkConnectionException -> {
                            syncCompletedExercises.scheduleCompleteExercises(
                                completeExercisesRequestData
                            )
                            CompleteExercisesStatus.Success
                        }
                        retryCount == 0 -> CompleteExercisesStatus.ServiceUnavailable
                        else -> {
                            completeExercises(completeExercisesRequestData, retryCount - 1)
                        }
                    }
                } else
                    CompleteExercisesStatus.Failure
            }
        } else
            CompleteExercisesStatus.Success

}