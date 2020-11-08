package com.studita.data.repository.datasource.user_data

import com.studita.data.database.user_data.UserDataDao
import com.studita.data.entity.UserDataEntity
import com.studita.data.net.UserDataService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException
import java.util.*

class CloudUserDataDataStore(
    private val connectionManager: ConnectionManager,
    private val userDataService: UserDataService,
    private val userDataDao: UserDataDao
) : UserDataDataStore {

    override suspend fun getUserDataEntity(userId: Int?): Pair<Int, UserDataEntity> {
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
                return userDataAsync.code() to entity
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

}