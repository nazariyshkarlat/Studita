package com.example.studita.domain.repository

import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.entity.UserDataData


interface UserDataRepository{

    suspend fun getUserData(userId: String, userToken: String): Pair<Int, UserDataData>

}