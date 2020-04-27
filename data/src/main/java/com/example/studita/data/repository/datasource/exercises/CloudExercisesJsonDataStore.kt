package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.entity.exercise.ExerciseArrayEntity
import com.example.studita.data.net.ExercisesService
import com.example.studita.data.net.OfflineExercisesService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class CloudExercisesJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val  exercisesService: ExercisesService,
    private val offlineExercisesService: OfflineExercisesService
) : ExercisesJsonDataStore {

    override suspend fun getExercisesJson(chapterPartNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val exercisesAsync = exercisesService.getExercisesAsync(chapterPartNumber)
            val result = exercisesAsync.await()
            return result.code() to result.body()!!.toString()
        }
    }

    suspend fun getOfflineExercisesJson(): Pair<Int, String>{
        val response =  offlineExercisesService.getOfflineExercisesAsync().await()
        return response.code() to response.body().toString()
    }
}