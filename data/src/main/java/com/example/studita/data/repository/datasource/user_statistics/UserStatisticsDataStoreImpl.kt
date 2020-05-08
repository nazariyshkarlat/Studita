package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.enum.UserStatisticsTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UserStatisticsDataStoreImpl(private val useStatisticsJsonDataStore: UserStatisticsJsonDataStore) : UserStatisticsDataStore{

    private val type: Type = object : TypeToken<List<UserStatisticsEntity>>() {}.type

    override suspend fun getUserStatisticsEntity(userIdToken: UserIdToken): Pair<Int, List<UserStatisticsEntity>>{
        val pair = useStatisticsJsonDataStore.getUserStatisticsJson(userIdToken)
        return pair.first to Gson().fromJson<List<UserStatisticsEntity>>(pair.second, type)
    }

}