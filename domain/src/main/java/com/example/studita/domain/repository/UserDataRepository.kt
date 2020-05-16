package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData


interface UserDataRepository{

    suspend fun getUserData(userIdTokenData: UserIdTokenData?, offlineMode: Boolean): Pair<Int, UserDataData>

    suspend fun saveUserData(userDataData: UserDataData)

    suspend fun deleteUserData()
}