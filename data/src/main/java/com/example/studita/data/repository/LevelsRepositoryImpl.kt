package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.levels.LevelsDataStore
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsDataStoreFactoryImpl,
    private val levelDataMapper: LevelDataMapper,
    private val connectionManager: ConnectionManager
) : LevelsRepository{

    override suspend fun getLevels(): List<LevelData> {
        return levelDataMapper.map(LevelsDataStore(levelsDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) LevelsJsonDataStoreFactory.Priority.CACHE else LevelsJsonDataStoreFactory.Priority.CLOUD)).getLevelsEntityList())
    }

}