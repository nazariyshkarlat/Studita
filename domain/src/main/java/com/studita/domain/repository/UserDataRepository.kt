package com.studita.domain.repository

import com.studita.domain.entity.UserDataData


interface UserDataRepository {

    suspend fun getUserData(userId: Int?, offlineMode: Boolean, isMyUserData: Boolean): Pair<Int, UserDataData>

    suspend fun saveUserData(userDataData: UserDataData)

    suspend fun deleteUserData()
}