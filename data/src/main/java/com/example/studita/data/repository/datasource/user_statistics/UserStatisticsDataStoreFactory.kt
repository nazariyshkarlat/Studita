package com.example.studita.data.repository.datasource.user_statistics

class UserStatisticsJsonDataStoreFactoryImpl(
    private val cloudUserStatisticsStatisticsDataStore: CloudUserStatisticsJsonDataStore
) : UserStatisticsJsonDataStoreFactory {

    override fun create() = cloudUserStatisticsStatisticsDataStore

}

interface UserStatisticsJsonDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(): UserStatisticsJsonDataStore
}