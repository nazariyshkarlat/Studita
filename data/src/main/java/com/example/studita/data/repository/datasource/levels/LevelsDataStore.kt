package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.level.LevelEntity
import com.example.studita.data.entity.level.LevelsDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class LevelsDataStore(private val levelsJsonDataStore: LevelsJsonDataStore) {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: LevelsDeserializer = LevelsDeserializer()
    private val gson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(LevelEntity::class.java, deserializer)
        gson = gsonBuilder.create()
        type = object : TypeToken<List<LevelEntity>>() {}.type
    }

    suspend fun getLevelsEntityList(): List<LevelEntity>{
        val json = levelsJsonDataStore.getLevelsJson()
        return gson.fromJson<List<LevelEntity>>(json, type)
    }

}