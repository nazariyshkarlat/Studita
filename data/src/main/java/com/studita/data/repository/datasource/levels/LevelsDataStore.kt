package com.studita.data.repository.datasource.levels

import com.studita.data.entity.level.LevelEntity

interface LevelsDataStore {
    suspend fun getLevelsEntityList(): List<LevelEntity>
}