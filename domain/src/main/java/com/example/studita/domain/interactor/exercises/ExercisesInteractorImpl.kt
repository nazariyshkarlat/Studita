package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.ExercisesCacheStatus
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.repository.ExercisesRepository
import kotlinx.coroutines.delay

class ExercisesInteractorImpl(
    private val repository: ExercisesRepository
) : ExercisesInteractor {

    private val retryDelay = 1000L

    override suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean,
        retryCount: Int
    ): ExercisesStatus =
        try {
            val results = repository.getExercises(chapterPartNumber, offlineMode)

            if (results.first == 200)
                ExercisesStatus.Success(results.second)
            else
                ExercisesStatus.NoChapterPartFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        ExercisesStatus.NoConnection
                    else
                        ExercisesStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getExercises(chapterPartNumber, offlineMode, retryCount - 1)
                }
            } else
                ExercisesStatus.Failure
        }

    override suspend fun downloadOfflineExercises(retryCount: Int): ExercisesCacheStatus =
        try {
            when (repository.downloadOfflineExercises()) {
                200 -> ExercisesCacheStatus.Success
                409 -> ExercisesCacheStatus.IsCached
                else -> ExercisesCacheStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        ExercisesCacheStatus.NoConnection
                    else
                        ExercisesCacheStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    downloadOfflineExercises(retryCount - 1)
                }
            } else
                ExercisesCacheStatus.Failure
        }

}