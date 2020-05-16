package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.repository.ExerciseResultRepository
import java.lang.Exception
import kotlinx.coroutines.delay

class ExerciseResultInteractorImpl(
    private val repository: ExerciseResultRepository
): ExerciseResultInteractor {

    private val retryDelay = 1000L

    override suspend fun getExerciseResult(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData,
        offlineMode: Boolean,
        retryCount: Int
    ): ExerciseResultStatus {
        if (offlineMode) {
            return ExerciseResultStatus.Success(
                repository.formExerciseResponse(exerciseData, exerciseRequestData)
            )
        } else {
            try {
                val result =
                    repository.getExerciseResult(exerciseData.exerciseNumber, exerciseRequestData)
                return if (result.first == 200)
                    ExerciseResultStatus.Success(result.second)
                else
                    ExerciseResultStatus.NoExerciseFound
            } catch (e: Exception) {
                e.printStackTrace()
                return if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        ExerciseResultStatus.NoConnection
                    else
                        ExerciseResultStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getExerciseResult(exerciseData, exerciseRequestData, offlineMode, retryCount-1)
                }
            }
        }
    }

}