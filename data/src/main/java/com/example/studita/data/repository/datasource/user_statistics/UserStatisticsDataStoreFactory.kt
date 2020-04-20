package com.example.studita.data.repository.datasource.user_statistics

class UserStatisticsDataStoreFactoryImpl(
    private val cloudUserStatisticsDataStore: CloudUserStatisticsDataStore,
    private val diskUserStatisticsDataStore: DiskUserStatisticsDataStore
) : UserStatisticsDataStoreFactory {

    override fun create(priority: UserStatisticsDataStoreFactory.Priority) =
        if (priority == UserStatisticsDataStoreFactory.Priority.CLOUD)
            cloudUserStatisticsDataStore
        else
            diskUserStatisticsDataStore
}

interface UserStatisticsDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): UserStatisticsDataStore
}