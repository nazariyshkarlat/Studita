package com.studita.data.repository.datasource.levels

import com.studita.data.entity.level.LevelEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LevelsDataStoreImpl(private val levelsJsonDataStore: LevelsJsonDataStore) : LevelsDataStore {

    override suspend fun getLevelsEntityList() = Json.decodeFromString<List<LevelEntity>>(levelsJsonDataStore.getLevelsJson())

}