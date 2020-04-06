package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.entity.UserDataEntity

interface UserDataDataStore {
    suspend fun getUserDataEntity(userId: String, userToken: String): Pair<Int, UserDataEntity>
}