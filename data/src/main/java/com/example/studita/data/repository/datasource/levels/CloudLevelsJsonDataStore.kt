package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.cache.levels.LevelsCache
import com.example.studita.data.entity.level.LevelsDeserializer
import com.example.studita.data.entity.level.LevelEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.LevelsService
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.lang.reflect.Type

class CloudLevelsJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val  levelsService: LevelsService
) : LevelsJsonDataStore {

    override suspend fun getLevelsJson(isLoggedIn: Boolean): String =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            try {
                val launchesAsync = levelsService.getLevelsAsync(isLoggedIn)
                val result = launchesAsync.await()
                val body = result.body()!!
                body.toString()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
}