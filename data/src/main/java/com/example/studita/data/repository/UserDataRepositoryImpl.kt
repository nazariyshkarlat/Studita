package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_data.DiskUserDataDataStore
import com.example.studita.data.repository.datasource.user_data.UserDataDataStoreFactory
import com.example.studita.data.repository.datasource.user_data.UserDataJsonDataStoreFactoryImpl
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.repository.UserDataRepository

class UserDataRepositoryImpl(
    private val userDataDataStoreFactory: UserDataJsonDataStoreFactoryImpl,
    private val connectionManager: ConnectionManager
) : UserDataRepository {

    override suspend fun getUserData(userId: Int?, offlineMode: Boolean): Pair<Int, UserDataData> {
        val pair = userDataDataStoreFactory.create(
            if (offlineMode || (userId == null) || connectionManager.isNetworkAbsent())
                UserDataDataStoreFactory.Priority.CACHE else UserDataDataStoreFactory.Priority.CLOUD
        )
            .getUserDataEntity(userId)
        return pair.first to pair.second.toBusinessEntity()
    }

    override suspend fun saveUserData(userDataData: UserDataData) {
        (userDataDataStoreFactory.create(UserDataDataStoreFactory.Priority.CACHE) as DiskUserDataDataStore).saveUserDataEntity(
            userDataData.toRawEntity()
        )
    }

    override suspend fun deleteUserData() {
        (userDataDataStoreFactory.create(UserDataDataStoreFactory.Priority.CACHE) as DiskUserDataDataStore).deleteUserData()
    }

}