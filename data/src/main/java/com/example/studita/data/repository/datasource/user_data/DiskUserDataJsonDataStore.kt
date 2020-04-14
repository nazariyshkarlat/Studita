package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.cache.user_data.UserDataCache
import com.example.studita.data.entity.UserTokenId

class DiskUserDataJsonDataStore(private val userDataCache: UserDataCache) : UserDataJsonDataStore{
    override suspend fun getUserDataJson(userTokenId: UserTokenId): Pair<Int, String?> = 200 to userDataCache.getUserDataJson()
}