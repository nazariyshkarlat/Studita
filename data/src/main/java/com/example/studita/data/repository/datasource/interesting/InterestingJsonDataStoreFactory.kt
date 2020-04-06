package com.example.studita.data.repository.datasource.interesting

class InterestingJsonDataStoreFactoryImpl(
    private val cloudInterestingJsonDataStore: CloudInterestingJsonDataStore,
    private val diskInterestingJsonDataStore: DiskInterestingJsonDataStore
) : InterestingJsonDataStoreFactory{

    override fun create(priority: InterestingJsonDataStoreFactory.Priority) =
        if (priority == InterestingJsonDataStoreFactory.Priority.CLOUD)
            cloudInterestingJsonDataStore
        else
            diskInterestingJsonDataStore
}

interface InterestingJsonDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): InterestingJsonDataStore
}