package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.UserDataStatus

interface UserDataInteractor {

    suspend fun getUserData(userId: Int?, offlineMode: Boolean, retryCount: Int = 30) : UserDataStatus

    suspend fun saveUserData(userDataData: UserDataData)

    suspend fun deleteUserData()

}