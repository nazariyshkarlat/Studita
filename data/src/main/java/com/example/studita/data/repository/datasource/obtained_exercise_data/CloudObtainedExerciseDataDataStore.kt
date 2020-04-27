package com.example.studita.data.repository.datasource.obtained_exercise_data

import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataRequestEntity
import com.example.studita.data.net.ObtainedExerciseDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.util.*

class CloudObtainedExerciseDataDataStore(private val connectionManager: ConnectionManager, private val obtainedExerciseDataService: ObtainedExerciseDataService) : ObtainedExerciseDataDataStore{
    override suspend fun trySaveData(
        userIdToken: UserIdToken,
        obtainedExerciseDataEntity: ObtainedExerciseDataEntity
    ): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val responseCode: Int
            try {
                val saveDataAsync = obtainedExerciseDataService.saveObtainedExerciseDataAsync(
                    DateTimeFormat().format(Date()),
                    ObtainedExerciseDataRequestEntity(
                        userIdToken,
                        obtainedExerciseDataEntity
                    )
                )
                val result = saveDataAsync.await()
                responseCode = result.code()
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
            responseCode
        }

}