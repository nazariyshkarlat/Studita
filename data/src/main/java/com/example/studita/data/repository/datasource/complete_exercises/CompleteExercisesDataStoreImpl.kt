package com.example.studita.data.repository.datasource.complete_exercises

import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.data.net.CompleteExercisesService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class CompleteExercisesDataStoreImpl(private val connectionManager: ConnectionManager, private val completeExercisesService: CompleteExercisesService) : CompleteExercisesPartDataStore{
    override suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int {
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else {
            try {
                val response =
                    completeExercisesService.completeExercisesAsync(completeExercisesRequest)
                return response.await().code()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

}