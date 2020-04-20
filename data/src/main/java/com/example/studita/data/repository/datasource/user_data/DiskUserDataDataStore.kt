package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserTokenId

class DiskUserDataDataStore(private val userDataDao: UserDataDao)
    : UserDataDataStore{

    override suspend fun getUserDataEntity(userTokenId: UserTokenId): Pair<Int, UserDataEntity> = 200 to userDataDao.getUserDataAsync()

    suspend fun saveUserDataEntity(userDataEntity: UserDataEntity) = userDataDao.insertUserData(userDataEntity)
}