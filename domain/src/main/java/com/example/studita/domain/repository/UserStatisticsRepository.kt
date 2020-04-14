package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userTokenId: UserTokenIdData, time: UserStatisticsTime): Pair<Int, UserStatisticsData>

}