package com.example.studita.domain.repository

import com.example.studita.domain.entity.LevelsDataData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData

interface LevelsDataRepository{
    suspend fun getLevelsData(userTokenIdData: UserTokenIdData): Pair<Int, LevelsDataData>
}