package com.studita.data.repository.datasource.exercises

import com.studita.data.entity.exercise.*
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.data.net.ExerciseReportService
import java.lang.Exception
import java.lang.reflect.Type

class ExerciseReportDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val exerciseReportService: ExerciseReportService
) : ExerciseReportDataStore {

    override suspend fun sendExerciseReport(exerciseReportRequest: ExerciseReportRequest): Int {
            if (connectionManager.isNetworkAbsent()) {
                throw NetworkConnectionException()
            } else {
                try {
                    return exerciseReportService.sendExerciseReport(exerciseReportRequest).code()
                }catch (e: Exception){
                    e.printStackTrace()
                    throw ServerUnavailableException()
                }
        }
    }

}