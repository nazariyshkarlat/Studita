package com.example.studita.domain.repository

import com.example.studita.domain.entity.LevelData

interface LevelsRepository{

    suspend fun getLevels(): List<LevelData>

}