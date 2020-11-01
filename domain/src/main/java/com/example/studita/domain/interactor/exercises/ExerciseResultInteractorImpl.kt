package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseReportData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.ExerciseReportStatus
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.interactor.InterestingLikeStatus
import com.example.studita.domain.repository.ExerciseResultRepository
import com.example.studita.domain.service.SyncExercisesReports
import kotlinx.coroutines.delay

class ExerciseResultInteractorImpl(
    private val repository: ExerciseResultRepository,
    private val syncExercisesReports: SyncExercisesReports
) : ExerciseResultInteractor {

    private val retryDelay = 1000L

    override suspend fun getExerciseResult(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData,
        offlineMode: Boolean,
        retryCount: Int
    ): ExerciseResultStatus =
        if (offlineMode) {
            ExerciseResultStatus.Success(
                repository.formExerciseResponse(exerciseData, exerciseRequestData)
            )
        } else {
            try {
                val result =
                    repository.getExerciseResult(exerciseData.exerciseNumber, exerciseRequestData)
                if (result.first == 200)
                    ExerciseResultStatus.Success(result.second)
                else
                    ExerciseResultStatus.NoExerciseFound
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException || e is ServerUnavailableException) {
                    if (retryCount == 0) {
                        if (e is NetworkConnectionException)
                            ExerciseResultStatus.NoConnection
                        else
                            ExerciseResultStatus.ServiceUnavailable
                    } else {
                        delay(retryDelay)
                        getExerciseResult(
                            exerciseData,
                            exerciseRequestData,
                            offlineMode,
                            retryCount - 1
                        )
                    }
                } else
                    ExerciseResultStatus.Failure
            }
        }

    override suspend fun sendExerciseReport(
        exerciseReportRequestData: ExerciseReportRequestData,
        retryCount: Int
    ) : ExerciseReportStatus =
        try {
        if (repository.sendExerciseReport(exerciseReportRequestData) == 200)
            ExerciseReportStatus.Success
        else
            ExerciseReportStatus.Failure
    } catch (e: Exception) {
        e.printStackTrace()
        if (e is NetworkConnectionException || e is ServerUnavailableException) {
            if (retryCount == 0) {
                if(e is NetworkConnectionException) {
                    syncExercisesReports.scheduleSendExerciseReport(
                        exerciseReportRequestData
                    )
                    ExerciseReportStatus.NoConnection
                }else
                    ExerciseReportStatus.ServiceUnavailable
            }
            else {
                delay(retryDelay)
                sendExerciseReport(exerciseReportRequestData, retryCount - 1)
            }
        } else
            ExerciseReportStatus.Failure
    }

}