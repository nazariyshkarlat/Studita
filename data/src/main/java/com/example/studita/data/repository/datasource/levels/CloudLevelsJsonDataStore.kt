package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.LevelsService
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class CloudLevelsJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val  levelsService: LevelsService
) : LevelsJsonDataStore {

    override suspend fun getLevelsJson(isLoggedIn: Boolean): String =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            try {
                val levels = levelsService.getLevels(isLoggedIn)
                levels.body()!!.toString()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
}