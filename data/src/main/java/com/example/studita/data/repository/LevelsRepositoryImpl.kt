package com.example.studita.data.repository

import com.example.studita.data.cache.levels.LevelsCache
import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.levels.*
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsJsonDataStoreFactory,
    private val levelDataMapper: LevelDataMapper,
    private val connectionManager: ConnectionManager,
    private val levelsCache: LevelsCache
) : LevelsRepository{

    override suspend fun getLevels(isLoggedIn: Boolean): List<LevelData> {
        return levelDataMapper.map(LevelsDataStoreImpl(levelsDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) LevelsJsonDataStoreFactory.Priority.CACHE else LevelsJsonDataStoreFactory.Priority.CLOUD)).getLevelsEntityList(isLoggedIn))
    }

    override suspend fun downloadLevels(): Int {
        var code = 409
        if (!levelsCache.isCached(true)) {
            val levelsJson =
                (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CLOUD) as CloudLevelsJsonDataStore).getLevelsJson(
                    true
                )
            levelsCache.saveLevelsJson(true, levelsJson)
            code = 200
        }
        if (!levelsCache.isCached(false)) {
            val levelsJson =
                (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CLOUD) as CloudLevelsJsonDataStore).getLevelsJson(
                    false
                )
            levelsCache.saveLevelsJson(false, levelsJson)
            code = 200
        }
        return code
    }

}