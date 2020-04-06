package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.entity.UserDataEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UserDataDataStoreImpl(private val userDataJsonDataStore: UserDataJsonDataStore) : UserDataDataStore{

    private val type: Type = object : TypeToken<UserDataEntity>() {}.type

    override suspend fun getUserDataEntity(userId: String, userToken: String): Pair<Int, UserDataEntity>{
        val pair = userDataJsonDataStore.getUserDataJson(userId, userToken)
        return pair.first to Gson().fromJson<UserDataEntity>(pair.second, type)
    }

}