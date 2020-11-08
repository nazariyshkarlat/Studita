package com.studita.data.repository.datasource.levels

class LevelsJsonDataStoreFactoryImpl(
    private val cloudLaunchesDataStore: CloudLevelsJsonDataStore,
    private val diskLevelsDataStore: DiskLevelsJsonDataStore
) : LevelsJsonDataStoreFactory {

    override fun create(priority: LevelsJsonDataStoreFactory.Priority) =
        if (priority == LevelsJsonDataStoreFactory.Priority.CLOUD)
            cloudLaunchesDataStore
        else
            diskLevelsDataStore
}

interface LevelsJsonDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): LevelsJsonDataStore
}