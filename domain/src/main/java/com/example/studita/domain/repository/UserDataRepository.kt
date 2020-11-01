package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData


interface UserDataRepository {

    suspend fun getUserData(userId: Int?, offlineMode: Boolean, isMyUserData: Boolean): Pair<Int, UserDataData>

    suspend fun saveUserData(userDataData: UserDataData)

    suspend fun deleteUserData()
}