package com.studita.data.repository.datasource.exercises

import com.studita.data.net.ExercisesService
import com.studita.data.net.OfflineExercisesService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class CloudExercisesJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val exercisesService: ExercisesService,
    private val offlineExercisesService: OfflineExercisesService
) : ExercisesJsonDataStore {

    override suspend fun getExercisesJson(chapterPartNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val exercises = exercisesService.getExercises(chapterPartNumber)
                return exercises.code() to exercises.body()!!.toString()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    suspend fun getOfflineExercisesJson(): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val response = offlineExercisesService.getOfflineExercises()
                return response.code() to response.body()!!.toString()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }
}