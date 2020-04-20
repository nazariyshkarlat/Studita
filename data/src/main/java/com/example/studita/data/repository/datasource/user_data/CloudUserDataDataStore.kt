package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.exception.NetworkConnectionException
import java.util.*

class CloudUserDataDataStore(private val connectionManager: ConnectionManager, private val userDataService: UserDataService, private val userDataDao: UserDataDao) : UserDataDataStore{

    override suspend fun getUserDataEntity(userTokenId: UserTokenId): Pair<Int, UserDataEntity>{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val userDataAsync = userDataService.getUserDataAsync(DateTimeFormat().format(Date()), userTokenId)
            val result = userDataAsync.await()
            val entity = result.body()!!
            userDataDao.insertUserData(entity)
            return result.code() to entity
        }
    }

}