package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData


interface UserDataRepository{

    suspend fun getUserData(userTokenIdData: UserTokenIdData): Pair<Int, UserDataData>

    suspend fun saveUserData(userDataData: UserDataData)
}