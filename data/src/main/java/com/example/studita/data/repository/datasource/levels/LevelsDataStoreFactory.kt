package com.example.studita.data.repository.datasource.levels

class LevelsDataStoreFactoryImpl(
    private val cloudLaunchesDataStore: CloudLevelsDataStore
) : LevelsDataStoreFactory {

    override fun create(priority: LevelsDataStoreFactory.Priority) =
        if (priority == LevelsDataStoreFactory.Priority.CLOUD)
            cloudLaunchesDataStore
        else
            cloudLaunchesDataStore
}

interface LevelsDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): LevelsDataStore
}