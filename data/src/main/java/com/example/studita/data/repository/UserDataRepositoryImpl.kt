package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.UserDataDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.levels.LevelsDataStore
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory
import com.example.studita.data.repository.datasource.user_data.UserDataDataStore
import com.example.studita.data.repository.datasource.user_data.UserDataDataStoreFactory
import com.example.studita.data.repository.datasource.user_data.UserDataDataStoreFactoryImpl
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.repository.UserDataRepository

class UserDataRepositoryImpl(private val userDataDataStoreFactory: UserDataDataStoreFactoryImpl, private val userDataDataMapper: UserDataDataMapper, private val connectionManager: ConnectionManager) : UserDataRepository{

    override suspend fun getUserData(userId: String, userToken: String) : Pair<Int, UserDataData> {
        val pair =  UserDataDataStore(userDataDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserDataDataStoreFactory.Priority.CACHE else UserDataDataStoreFactory.Priority.CLOUD)).getUserDataEntity(userId, userToken)
        return pair.first to userDataDataMapper.map(pair.second)
    }

}