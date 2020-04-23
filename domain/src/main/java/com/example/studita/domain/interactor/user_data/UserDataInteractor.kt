package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.UserDataStatus

interface UserDataInteractor {

    suspend fun getUserData(userIdTokenData: UserIdTokenData?) : UserDataStatus

    suspend fun saveUserData(userDataData: UserDataData)

}