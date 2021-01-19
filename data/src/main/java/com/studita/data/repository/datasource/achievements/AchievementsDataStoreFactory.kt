package com.studita.data.repository.datasource.achievements

import com.studita.data.repository.datasource.authorization.AuthorizationDataStore

class AchievementsDataStoreFactoryImpl(
    private val achievementsDataStoreImpl: AchievementsDataStoreImpl
) : AchievementsDataStoreFactory {

    override fun create() =
        achievementsDataStoreImpl
}

interface AchievementsDataStoreFactory {

    fun create(): AchievementsDataStore
}