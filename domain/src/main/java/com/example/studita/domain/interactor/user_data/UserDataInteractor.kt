package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.interactor.UserDataStatus

interface UserDataInteractor {

    suspend fun getUserData(userTokenIdData: UserTokenIdData) : UserDataStatus

    suspend fun saveUserData(userDataData: UserDataData)

}