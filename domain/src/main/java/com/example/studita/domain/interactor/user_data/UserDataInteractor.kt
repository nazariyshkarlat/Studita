package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.interactor.UserDataStatus

interface UserDataInteractor {

    suspend fun getUserData(userId: String, userToken: String) : UserDataStatus

}