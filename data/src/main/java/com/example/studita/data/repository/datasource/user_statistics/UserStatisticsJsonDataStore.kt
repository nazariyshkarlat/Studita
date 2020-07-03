package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserIdToken

interface UserStatisticsJsonDataStore {
    suspend fun getUserStatisticsJson(userId: Int) : Pair<Int, String?>
}