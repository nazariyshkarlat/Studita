package com.example.studita.data.repository.datasource.user_statistics

class UserStatisticsJsonDataStoreFactoryImpl(
    private val cloudUserStatisticsStatisticsStore: CloudUserStatisticsJsonDataStore,
    private val diskUserStatisticsJsonStatisticsStore: DiskUserStatisticsJsonDataStore
) : UserStatisticsDataStoreFactory {

    override fun create(priority: UserStatisticsDataStoreFactory.Priority) =
        if (priority == UserStatisticsDataStoreFactory.Priority.CLOUD)
            cloudUserStatisticsStatisticsStore
        else
            diskUserStatisticsJsonStatisticsStore
}

interface UserStatisticsDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): UserStatisticsJsonDataStore
}