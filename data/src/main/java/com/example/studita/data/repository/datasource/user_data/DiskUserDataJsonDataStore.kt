package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataCache

class DiskUserDataJsonDataStore(private val userDataCache: UserDataCache) : UserDataJsonDataStore{
    override suspend fun getUserDataJson(userId: String, userToken: String): Pair<Int, String?> = 200 to userDataCache.getUserDataJson()


}