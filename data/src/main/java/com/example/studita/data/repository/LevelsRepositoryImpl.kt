package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreFactory
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreFactoryImpl
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.repository.LevelsRepository

class LevelsRepositoryImpl(
    private val levelsDataStoreFactory: LevelsDataStoreFactoryImpl,
    private val levelDataMapper: LevelDataMapper
) : LevelsRepository{

    override suspend fun getLevels(): List<LevelData> {
        return levelDataMapper.map(levelsDataStoreFactory.create(LevelsDataStoreFactory.Priority.CLOUD).getLevelsEntityList())
    }

}