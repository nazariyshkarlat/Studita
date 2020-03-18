package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.level.LevelsDeserializer
import com.example.studita.data.entity.level.LevelEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.LevelsService
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CloudLevelsDataStore(
    private val connectionManager: ConnectionManager,
    private val  levelsService: LevelsService
) : LevelsDataStore {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: LevelsDeserializer = LevelsDeserializer()
    private val exercisesGson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(LevelEntity::class.java, deserializer)
        exercisesGson = gsonBuilder.create()
        type = object : TypeToken<List<LevelEntity>>() {}.type
    }


    override suspend fun getLevelsEntityList(): List<LevelEntity>  =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else{
            try {
                val launchesAsync = levelsService.getLevelsAsync()
                val result = launchesAsync.await()
                val body = result.body()!!
                exercisesGson.fromJson<List<LevelEntity>>(body, type)
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }
}