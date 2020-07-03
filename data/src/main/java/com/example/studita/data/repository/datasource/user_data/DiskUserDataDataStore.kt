package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.date.DateUtils
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken

class DiskUserDataDataStore(private val userDataDao: UserDataDao)
    : UserDataDataStore{

    override suspend fun getUserDataEntity(userId: Int?): Pair<Int, UserDataEntity> {
        val userData = userDataDao.getUserData()
        return 200 to if(userData == null) {
            saveUserDataEntity(UserDataDataStore.defaultUserData)
            UserDataDataStore.defaultUserData
        }else
            userData
    }


    suspend fun saveUserDataEntity(userDataEntity: UserDataEntity) {
        userDataDao.insertUserData(userDataEntity)
    }

    suspend fun deleteUserData(){
        userDataDao.deleteUserData()
    }
}