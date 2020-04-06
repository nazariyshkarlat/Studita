package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.repository.datasource.user_data.UserDataJsonDataStore
import com.example.studita.domain.enum.UserStatisticsTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UserStatisticsDataStoreImpl(private val useStatisticsJsonDataStore: UserStatisticsJsonDataStore) : UserStatisticsDataStore{

    private val type: Type = object : TypeToken<UserStatisticsEntity>() {}.type

    override suspend fun getUserStatisticsEntity(userId: String, userToken: String, time: UserStatisticsTime): Pair<Int, UserStatisticsEntity>{
        val pair = useStatisticsJsonDataStore.getUserStatisticsJson(userId, userToken, time)
        return pair.first to Gson().fromJson<UserStatisticsEntity>(pair.second, type)
    }

}