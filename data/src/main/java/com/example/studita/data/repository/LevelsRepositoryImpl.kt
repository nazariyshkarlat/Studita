package com.example.studita.data.repository

import com.example.studita.data.entity.level.toBusinessEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.DiskLevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreImpl
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsJsonDataStoreFactory,
    private val connectionManager: ConnectionManager
) : LevelsRepository {

    override suspend fun getLevels(offlineMode: Boolean): List<LevelData> {
        return LevelsDataStoreImpl(levelsDataStoreFactory.create(if (offlineMode) LevelsJsonDataStoreFactory.Priority.CACHE else LevelsJsonDataStoreFactory.Priority.CLOUD)).getLevelsEntityList().map { it.toBusinessEntity() }
    }

    override suspend fun downloadLevels(): Int {
        var code = 409
        val diskDataStore =
            (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CACHE) as DiskLevelsJsonDataStore)
        if (!diskDataStore.levelsAreCached()) {
            val levelsJson =
                (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CLOUD) as CloudLevelsJsonDataStore).getLevelsJson()
            diskDataStore.saveLevelsJson(levelsJson)
            code = 200
        }
        return code
    }

}