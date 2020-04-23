package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreImpl
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsJsonDataStoreFactory,
    private val levelDataMapper: LevelDataMapper,
    private val connectionManager: ConnectionManager
) : LevelsRepository{

    override suspend fun getLevels(isLoggedIn: Boolean): List<LevelData> {
        return levelDataMapper.map(LevelsDataStoreImpl(levelsDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) LevelsJsonDataStoreFactory.Priority.CACHE else LevelsJsonDataStoreFactory.Priority.CLOUD)).getLevelsEntityList(isLoggedIn))
    }

}