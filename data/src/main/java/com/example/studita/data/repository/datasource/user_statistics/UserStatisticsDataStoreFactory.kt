package com.example.studita.data.repository.datasource.user_statistics

class UserStatisticsJsonDataStoreFactoryImpl(
    private val cloudUserStatisticsStatisticsStore: CloudUserStatisticsJsonDataStore,
    private val diskUserStatisticsJsonStatisticsStore: DiskUserStatisticsJsonDataStore
) : UserStatisticsJsonDataStoreFactory {

    override fun create(priority: UserStatisticsJsonDataStoreFactory.Priority) =
        if (priority == UserStatisticsJsonDataStoreFactory.Priority.CLOUD)
            cloudUserStatisticsStatisticsStore
        else
            diskUserStatisticsJsonStatisticsStore
}

interface UserStatisticsJsonDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): UserStatisticsJsonDataStore
}