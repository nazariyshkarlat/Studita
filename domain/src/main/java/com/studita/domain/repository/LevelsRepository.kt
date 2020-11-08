package com.studita.domain.repository

import com.studita.domain.entity.LevelData

interface LevelsRepository {

    suspend fun getLevels(offlineMode: Boolean): List<LevelData>

    suspend fun downloadLevels(): Int

}