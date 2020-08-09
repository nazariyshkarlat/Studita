package com.example.studita.domain.repository

import com.example.studita.domain.entity.LevelData

interface LevelsRepository {

    suspend fun getLevels(isLoggedIn: Boolean, offlineMode: Boolean): List<LevelData>

    suspend fun downloadLevels(): Int

}