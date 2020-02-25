package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.repository.ExerciseResultRepository
import com.example.studita.domain.repository.ExercisesRepository
import java.lang.Exception

class ExerciseResultInteractorImpl(
    private val repository: ExerciseResultRepository
): ExerciseResultInteractor {

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestData: ExerciseRequestData
    ): ExerciseResultStatus =
        try{
        val result = repository.getExerciseResult(exerciseNumber, exerciseRequestData)
        if(result.first == 200)
            ExerciseResultStatus.Success(result.second)
        else
            ExerciseResultStatus.NoExerciseFound
    } catch (e: Exception) {
        e.printStackTrace()
        if (e is NetworkConnectionException)
            ExerciseResultStatus.NoConnection
        else
            ExerciseResultStatus.ServiceUnavailable
    }

}