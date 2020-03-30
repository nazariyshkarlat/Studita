package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.entity.level.LevelEntity

interface LevelsJsonDataStore {

    suspend fun getLevelsJson(): String

}