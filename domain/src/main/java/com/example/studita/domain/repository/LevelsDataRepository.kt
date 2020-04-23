package com.example.studita.domain.repository

import com.example.studita.domain.entity.LevelsDataData
import com.example.studita.domain.entity.UserIdTokenData

interface LevelsDataRepository{
    suspend fun getLevelsData(userIdTokenData: UserIdTokenData): Pair<Int, LevelsDataData>
}