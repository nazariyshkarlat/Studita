package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.date.DateUtils
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken
import kotlinx.coroutines.delay

class DiskUserDataDataStore(private val userDataDao: UserDataDao)
    : UserDataDataStore{

    companion object {
        private val defaultUserData = UserDataEntity(
            null,
            null,
            null,
            1,
            0,
            0,
            false,
            listOf(0, 0, 0, 0),
            DateUtils.getDefaultDateSting()
        )
    }

    override suspend fun getUserDataEntity(userIdToken: UserIdToken?): Pair<Int, UserDataEntity> {
        if(userDataDao.getUserDataAsync() == null) {
            saveUserDataEntity(defaultUserData)
        }
        return 200 to userDataDao.getUserDataAsync()!!
    }

    suspend fun saveUserDataEntity(userDataEntity: UserDataEntity) = userDataDao.insertUserData(userDataEntity)
}