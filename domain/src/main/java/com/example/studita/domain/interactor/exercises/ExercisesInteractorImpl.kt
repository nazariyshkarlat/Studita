package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.repository.ExercisesRepository
import java.lang.Exception

class ExercisesInteractorImpl(
    private val repository: ExercisesRepository
): ExercisesInteractor {

    override suspend fun getExercises(chapterPartNumber: Int): ExercisesStatus =
        try {
            val results = repository.getExercises(chapterPartNumber)

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

}