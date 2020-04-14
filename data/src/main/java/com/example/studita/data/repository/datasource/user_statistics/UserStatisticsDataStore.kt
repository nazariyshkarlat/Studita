package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.UserTokenId
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsDataStore {
    suspend fun getUserStatisticsEntity(userTokenId: UserTokenId, time: UserStatisticsTime): Pair<Int, UserStatisticsEntity>
}