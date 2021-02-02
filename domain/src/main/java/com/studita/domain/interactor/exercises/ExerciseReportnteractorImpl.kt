package com.studita.domain.interactor.exercises

import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.ExerciseReportStatus
import com.studita.domain.repository.ExerciseReportRepository
import com.studita.domain.service.SyncExercisesReports
import kotlinx.coroutines.delay

class ExerciseReportnteractorImpl(
    private val repository: ExerciseReportRepository,
    private val syncExercisesReports: SyncExercisesReports
) : ExerciseReportnteractor {

    private val retryDelay = 1000L

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