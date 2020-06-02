package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.entity.SaveUserDataRequest
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception
import java.util.*

class CloudUserDataDataStore(private val connectionManager: ConnectionManager, private val userDataService: UserDataService, private val userDataDao: UserDataDao) : UserDataDataStore{

    override suspend fun getUserDataEntity(userId: Int?): Pair<Int, UserDataEntity>{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val userDataAsync =
                    userDataService.getUserData(
                        DateTimeFormat().format(Date()),
                        userId!!
                    )
                val entity = userDataAsync.body()!!
                userDataDao.insertUserData(entity)
                return userDataAsync.code() to entity
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

}