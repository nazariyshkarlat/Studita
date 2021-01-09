package com.studita.data.repository

import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.repository.datasource.user_data.DiskUserDataDataStore
import com.studita.data.repository.datasource.user_data.UserDataDataStoreFactory
import com.studita.data.repository.datasource.user_data.UserDataDataStoreFactoryImpl
import com.studita.domain.entity.UserDataData
import com.studita.domain.repository.UserDataRepository

class UserDataRepositoryImpl(
    private val userDataDataStoreFactory: UserDataDataStoreFactoryImpl,
    private val connectionManager: ConnectionManager
) : UserDataRepository {

    override suspend fun getUserData(userId: Int?, offlineMode: Boolean, isMyUserData: Boolean): Pair<Int, UserDataData> {

        val priority = if ((offlineMode || userId == null) && isMyUserData)
            UserDataDataStoreFactory.Priority.CACHE
        else
            UserDataDataStoreFactory.Priority.CLOUD

        val pair = userDataDataStoreFactory.create(priority).getUserDataEntity(userId)

        if(isMyUserData && priority != UserDataDataStoreFactory.Priority.CACHE)
            saveUserData(pair.second.toBusinessEntity())

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