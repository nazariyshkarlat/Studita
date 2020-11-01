package com.example.studita.data.repository.datasource.complete_exercises

import com.example.studita.data.database.completed_exercises.CompletedExercisesDao
import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.data.entity.CompletedExercisesEntity
import com.example.studita.data.net.CompleteExercisesService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class CompleteExercisesDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val completeExercisesService: CompleteExercisesService,
    private val completedExercisesDao: CompletedExercisesDao
) : CompleteExercisesPartDataStore {
    override suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val response =
                    completeExercisesService.completeExercises(completeExercisesRequest)
                return response.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }


    override suspend fun addLocalCompletedExercises(completedExercisesEntity: CompletedExercisesEntity){
        completedExercisesDao.insertCompletedExercisesEntity(completedExercisesEntity)
    }

    override suspend fun getLocalCompletedExercises(): List<CompletedExercisesEntity>? {
        return completedExercisesDao.getCompletedExercisesEntity()
    }

    override suspend fun deleteLocalCompletedExercises(dateTimeCompleted: String) {
        completedExercisesDao.deleteCompletedExercises(dateTimeCompleted)
    }

    override suspend fun clearLocalCompletedExercises() {
        completedExercisesDao.clearCompletedExercises()
    }

}