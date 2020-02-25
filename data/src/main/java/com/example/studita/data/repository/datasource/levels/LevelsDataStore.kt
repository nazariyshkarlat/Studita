package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.LevelEntity

interface LevelsDataStore {

    suspend fun getLevelsEntityList(): List<LevelEntity>

}