package com.studita.data.repository.datasource.user_statistics

interface UserStatisticsJsonDataStore {
    suspend fun getUserStatisticsJson(userId: Int): Pair<Int, String?>
}