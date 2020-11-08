package com.studita.data.repository.datasource.user_data

import android.util.Log
import com.studita.data.database.user_data.UserDataDao
import com.studita.data.entity.UserDataEntity
import java.lang.Exception

class DiskUserDataDataStore(private val userDataDao: UserDataDao) : UserDataDataStore {

    override suspend fun getUserDataEntity(userId: Int?): Pair<Int, UserDataEntity> {
        val userData = userDataDao.getUserData()
        return 200 to if (userData == null || userId != userData.userId) {
            saveUserDataEntity(UserDataDataStore.defaultUserData)
            UserDataDataStore.defaultUserData
        } else
            userData
    }


    suspend fun saveUserDataEntity(userDataEntity: UserDataEntity) {
        userDataDao.insertUserData(userDataEntity)
    }

    suspend fun deleteUserData() {
        userDataDao.deleteUserData()
    }
}