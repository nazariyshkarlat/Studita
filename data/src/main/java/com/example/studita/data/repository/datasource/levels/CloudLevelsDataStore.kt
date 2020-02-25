package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.LevelEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.LevelsService
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class CloudLevelsDataStore(
    private val connectionManager: ConnectionManager,
    private val  levelsService: LevelsService
) : LevelsDataStore {

    override suspend fun getLevelsEntityList(): List<LevelEntity>  =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else{
            try {
                val launchesAsync = levelsService.getLevelsAsync()
                val result = launchesAsync.await()
                result.body()!!
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }
}