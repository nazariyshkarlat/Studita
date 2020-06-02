package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsJsonDataStore {
    suspend fun getUserStatisticsJson(userId: Int) : Pair<Int, String?>
}