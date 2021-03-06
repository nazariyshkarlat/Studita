package com.studita.domain.interactor.complete_chapter_part

import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.CompleteExercisesStatus
import com.studita.domain.repository.CompleteExercisesRepository
import com.studita.domain.service.SyncCompletedExercises
import kotlinx.coroutines.delay
import java.util.*

class CompleteExercisesInteractorImpl(
    private val repository: CompleteExercisesRepository,
    private val syncCompletedExercises: SyncCompletedExercises
) : CompleteExercisesInteractor {

    private val retryDelay = 1000L

    override suspend fun completeExercises(
        completeExercisesRequestData: CompleteExercisesRequestData,
        retryCount: Int
    ): CompleteExercisesStatus =
        if (completeExercisesRequestData.userIdToken != null) {
            try {
                when (repository.completeExercises(completeExercisesRequestData)) {
                    200 -> {
                        deleteLocalCompletedExercises(completeExercisesRequestData.completedExercisesData.datetime)
                        CompleteExercisesStatus.Success
                    }
                    else -> CompleteExercisesStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException || e is ServerUnavailableException) {
                    if (retryCount == 0) {
                        if(e is NetworkConnectionException) {
                            syncCompletedExercises.scheduleCompleteExercises(
                                completeExercisesRequestData
                            )
                            CompleteExercisesStatus.NoConnection
                        }else
                            CompleteExercisesStatus.ServiceUnavailable
                    }
                    else {
                        delay(retryDelay)
                        completeExercises(completeExercisesRequestData, retryCount - 1)
                    }
                } else
                    CompleteExercisesStatus.Failure
            }
        } else
            CompleteExercisesStatus.Success


    override suspend fun syncCompleteLocalExercises(userIdTokenData: UserIdTokenData) : Boolean {
        return try {
            repository.getLocalCompletedExercises()?.forEach {
                val response = completeExercises(CompleteExercisesRequestData(userIdTokenData, it))
                if(response != CompleteExercisesStatus.Success) {
                    if(response == CompleteExercisesStatus.Failure){
                        deleteLocalCompletedExercises(it.datetime)
                    }else
                        return false
                }else
                    deleteLocalCompletedExercises(it.datetime)
            }
            return true
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteLocalCompletedExercises(completedDateTime: Date) {
        repository.deleteLocalCompletedExercises(completedDateTime)
    }

    override suspend fun addLocalCompletedExercises(completedExercisesData: CompletedExercisesData) {
        repository.addLocalCompletedExercises(completedExercisesData)
    }

    override suspend fun clearLocalCompletedExercises() {
        repository.clearLocalCompletedExercises()
    }

}