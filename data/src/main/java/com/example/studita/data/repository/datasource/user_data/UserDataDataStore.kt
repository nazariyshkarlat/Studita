package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken

interface UserDataDataStore {
    suspend fun getUserDataEntity(userIdToken: UserIdToken?): Pair<Int, UserDataEntity>
}