package com.studita.data.repository.datasource.user_statistics

import com.studita.data.entity.UserStatisticsEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UserStatisticsDataStoreImpl(private val useStatisticsJsonDataStore: UserStatisticsJsonDataStore) :
    UserStatisticsDataStore {
    
    override suspend fun getUserStatisticsEntity(userId: Int) =
        useStatisticsJsonDataStore.getUserStatisticsJson(userId).let {
            it.first to (it.second?.let {it1-> Json.decodeFromString<List<UserStatisticsEntity>>(it1) } ?: emptyList())
        }
}