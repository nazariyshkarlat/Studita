package com.example.studita.data.repository.datasource.user_data

import android.service.autofill.UserData
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserTokenId

interface UserDataJsonDataStore {
    suspend fun getUserDataJson(userTokenId: UserTokenId) : Pair<Int, String?>
}