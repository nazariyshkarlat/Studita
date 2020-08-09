package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserStatisticsEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UserStatisticsDataStoreImpl(private val useStatisticsJsonDataStore: UserStatisticsJsonDataStore) :
    UserStatisticsDataStore {

    private val type: Type = object : TypeToken<List<UserStatisticsEntity>>() {}.type

    override suspend fun getUserStatisticsEntity(userId: Int): Pair<Int, List<UserStatisticsEntity>> {
        val pair = useStatisticsJsonDataStore.getUserStatisticsJson(userId)
        return pair.first to Gson().fromJson(pair.second, type)
    }

}