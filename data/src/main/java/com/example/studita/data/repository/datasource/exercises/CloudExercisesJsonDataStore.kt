package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.net.ExercisesService
import com.example.studita.data.net.OfflineExercisesService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class CloudExercisesJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val  exercisesService: ExercisesService,
    private val offlineExercisesService: OfflineExercisesService
) : ExercisesJsonDataStore {

    override suspend fun getExercisesJson(chapterPartNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val exercises = exercisesService.getExercises(chapterPartNumber)
                return exercises.code() to exercises.body()!!.toString()
            }catch (e: Exception){
                throw ServerUnavailableException()
            }
        }
    }

    suspend fun getOfflineExercisesJson(): Pair<Int, String>{
        val response =  offlineExercisesService.getOfflineExercises()
        return response.code() to response.body()!!.toString()
    }
}