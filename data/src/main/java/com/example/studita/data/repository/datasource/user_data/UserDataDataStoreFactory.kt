package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory

class UserDataDataStoreFactoryImpl(
    private val cloudUserDataDataStore: CloudUserDataJsonDataStore,
    private val diskUserDataJsonDataStore: DiskUserDataJsonDataStore
) : UserDataDataStoreFactory {

    override fun create(priority: UserDataDataStoreFactory.Priority) =
        if (priority == UserDataDataStoreFactory.Priority.CLOUD)
            cloudUserDataDataStore
        else
            diskUserDataJsonDataStore
}

interface UserDataDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): UserDataJsonDataStore
}