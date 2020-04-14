package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserTokenId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UserDataDataStoreImpl(private val userDataJsonDataStore: UserDataJsonDataStore) : UserDataDataStore{

    private val type: Type = object : TypeToken<UserDataEntity>() {}.type

    override suspend fun getUserDataEntity(userTokenId: UserTokenId): Pair<Int, UserDataEntity>{
        val pair = userDataJsonDataStore.getUserDataJson(userTokenId)
        return pair.first to Gson().fromJson<UserDataEntity>(pair.second, type)
    }

}