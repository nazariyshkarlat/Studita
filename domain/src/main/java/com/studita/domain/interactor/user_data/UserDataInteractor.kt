package com.studita.domain.interactor.user_data

import com.studita.domain.entity.UserDataData
import com.studita.domain.interactor.UserDataStatus

interface UserDataInteractor {

    suspend fun getUserData(
        userId: Int?,
        getFromLocalStorage: Boolean,
        isMyUserData: Boolean,
        retryCount: Int = 3
    ): UserDataStatus

    suspend fun saveUserData(userDataData: UserDataData)

    suspend fun deleteUserData()

}