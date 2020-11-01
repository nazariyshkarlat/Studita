package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.level.LevelEntity

interface LevelsDataStore {
    suspend fun getLevelsEntityList(): List<LevelEntity>
}