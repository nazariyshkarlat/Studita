package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsJsonDataStore {
    suspend fun getUserStatisticsJson(userId: String, userToken: String, userStatisticsTime: UserStatisticsTime) : Pair<Int, String?>
}