package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.date.DateUtils
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken

class DiskUserDataDataStore(private val userDataDao: UserDataDao)
    : UserDataDataStore{

    override suspend fun getUserDataEntity(userId: Int?): Pair<Int, UserDataEntity> {
        if(userDataDao.getUserData() == null) {
            saveUserDataEntity(UserDataDataStore.defaultUserData)
        }
        return 200 to userDataDao.getUserData()!!
    }


    suspend fun saveUserDataEntity(userDataEntity: UserDataEntity) {
        userDataDao.insertUserData(userDataEntity)
    }

    suspend fun deleteUserData(){
        userDataDao.deleteUserData()
    }
}