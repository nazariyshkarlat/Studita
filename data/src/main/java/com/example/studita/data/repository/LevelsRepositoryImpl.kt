package com.example.studita.data.repository

import com.example.studita.data.cache.levels.LevelsCache
import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactory
import com.example.studita.data.repository.datasource.chapter.DiskChapterJsonDataStore
import com.example.studita.data.repository.datasource.levels.*
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsJsonDataStoreFactory,
    private val levelDataMapper: LevelDataMapper,
    private val connectionManager: ConnectionManager
) : LevelsRepository{

    override suspend fun getLevels(isLoggedIn: Boolean, offlineMode: Boolean): List<LevelData> {
        return levelDataMapper.map(LevelsDataStoreImpl(levelsDataStoreFactory.create(if(offlineMode || connectionManager.isNetworkAbsent()) LevelsJsonDataStoreFactory.Priority.CACHE else LevelsJsonDataStoreFactory.Priority.CLOUD)).getLevelsEntityList(isLoggedIn))
    }

    override suspend fun downloadLevels(): Int {
        var code = 409
        val diskDataStore = (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CACHE) as DiskLevelsJsonDataStore)
        if (!diskDataStore.levelsAreCached(true)) {
            val levelsJson =
                (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CLOUD) as CloudLevelsJsonDataStore).getLevelsJson(
                    true
                )
            diskDataStore.saveLevelsJson(true, levelsJson)
            code = 200
        }
        if (!diskDataStore.levelsAreCached(false)) {
            val levelsJson =
                (levelsDataStoreFactory.create(LevelsJsonDataStoreFactory.Priority.CLOUD) as CloudLevelsJsonDataStore).getLevelsJson(
                    false
                )
            diskDataStore.saveLevelsJson(false, levelsJson)
            code = 200
        }
        return code
    }

}