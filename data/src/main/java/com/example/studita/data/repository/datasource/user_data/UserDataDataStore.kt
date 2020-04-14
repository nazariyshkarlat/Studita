package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserTokenId

interface UserDataDataStore {
    suspend fun getUserDataEntity(userTokenId: UserTokenId): Pair<Int, UserDataEntity>
}