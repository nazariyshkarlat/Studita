package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.interactor.ExercisesCacheStatus
import com.example.studita.domain.repository.ExercisesRepository
import java.lang.Exception

class ExercisesInteractorImpl(
    private val repository: ExercisesRepository
): ExercisesInteractor {

    override suspend fun getExercises(chapterPartNumber: Int, offlineMode: Boolean): ExercisesStatus =
        try {
            val results = repository.getExercises(chapterPartNumber, offlineMode)

            if(results.first == 200)
                ExercisesStatus.Success(results.second)
            else
                ExercisesStatus.NoChapterPartFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                ExercisesStatus.NoConnection
            else
                ExercisesStatus.ServiceUnavailable
        }

    override suspend fun downloadOfflineExercises(): ExercisesCacheStatus =
        try {
            val result = repository.downloadOfflineExercises()
            if(result == 200)
                ExercisesCacheStatus.Success
            else
                ExercisesCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                ExercisesCacheStatus.NoConnection
            else
                ExercisesCacheStatus.ServiceUnavailable
    }

}