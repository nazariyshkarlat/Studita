package com.studita.data.repository.datasource.user_data

class UserDataJsonDataStoreFactoryImpl(
    private val cloudUserDataDataStore: CloudUserDataDataStore,
    private val diskUserDataDataStore: DiskUserDataDataStore
) : UserDataDataStoreFactory {

    override fun create(priority: UserDataDataStoreFactory.Priority) =
        if (priority == UserDataDataStoreFactory.Priority.CLOUD)
            cloudUserDataDataStore
        else
            diskUserDataDataStore
}

interface UserDataDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): UserDataDataStore
}