package com.example.studita.data.repository.datasource.user_data

import android.service.autofill.UserData
import com.example.studita.data.entity.UserDataEntity

interface UserDataJsonDataStore {
    suspend fun getUserDataJson(userId: String, userToken: String) : Pair<Int, String?>
}