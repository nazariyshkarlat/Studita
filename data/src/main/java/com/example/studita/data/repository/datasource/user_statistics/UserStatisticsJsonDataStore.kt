package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserTokenId
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsJsonDataStore {
    suspend fun getUserStatisticsJson(userTokenId: UserTokenId, userStatisticsTime: UserStatisticsTime) : Pair<Int, String?>
}