package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.date.DateUtils
import com.example.studita.data.entity.SaveUserDataRequest
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken

interface UserDataDataStore {

    companion object {
        val defaultUserData = UserDataEntity(
            null,
            null,
            null,
            1,
            0,
            0,
            false,
            listOf(0, 0, 0, 0),
            DateUtils.getDefaultDateSting(),
            0
        )
    }

    suspend fun getUserDataEntity(userIdToken: UserIdToken?): Pair<Int, UserDataEntity>

}