package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.UserDataEntityMapper
import com.example.studita.data.entity.mapper.UserDataDataMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_data.*
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.UserDataRepository

class UserDataRepositoryImpl(private val userDataDataStoreFactory: UserDataJsonDataStoreFactoryImpl, private val userDataDataMapper: UserDataDataMapper, private val connectionManager: ConnectionManager, private val userDataEntityMapper: UserDataEntityMapper) : UserDataRepository{

    override suspend fun getUserData(userIdTokenData: UserIdTokenData?, offlineMode: Boolean) : Pair<Int, UserDataData> {
        val pair =  userDataDataStoreFactory.create(
            if(offlineMode || (userIdTokenData == null) || connectionManager.isNetworkAbsent())
                UserDataDataStoreFactory.Priority.CACHE else UserDataDataStoreFactory.Priority.CLOUD)
            .getUserDataEntity(userIdTokenData?.let{UserIdTokenMapper().map(it)})
        return pair.first to userDataDataMapper.map(pair.second)
    }

    override suspend fun saveUserData(userDataData: UserDataData) {
        (userDataDataStoreFactory.create(UserDataDataStoreFactory.Priority.CACHE) as DiskUserDataDataStore).saveUserDataEntity(userDataEntityMapper.map(userDataData))
    }

}