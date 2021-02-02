package com.studita.data.repository.datasource.complete_exercises

import com.studita.data.database.completed_exercises.CompletedExercisesDao
import com.studita.data.entity.CompleteExercisesRequest
import com.studita.data.entity.CompletedExercisesEntity
import com.studita.data.net.CompleteExercisesService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class CompleteExercisesDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val completeExercisesService: CompleteExercisesService,
    private val completedExercisesDao: CompletedExercisesDao
) : CompleteExercisesDataStore {
    override suspend fun completeExercises(completeExercisesRequest: CompleteExercisesRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val response =
                    completeExercisesService.completeExercises(completeExercisesRequest)
                return response.code()
            } catch (e: Exception) {
                e.printStackTrace()
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