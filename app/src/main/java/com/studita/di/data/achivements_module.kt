package com.studita.di.data

import com.studita.data.net.AchievementsService
import com.studita.data.repository.AchievementsRepositoryImpl
import com.studita.data.repository.datasource.achievements.AchievementsDataStore
import com.studita.data.repository.datasource.achievements.AchievementsDataStoreFactory
import com.studita.data.repository.datasource.achievements.AchievementsDataStoreFactoryImpl
import com.studita.data.repository.datasource.achievements.AchievementsDataStoreImpl
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactory
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.achievements.AchievementsInteractor
import com.studita.domain.interactor.achievements.AchievementsInteractorImpl
import com.studita.domain.repository.AchievementsRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createAchievementsModule(config: DI.Config) = configModule(configuration = config){

    single {
        AchievementsInteractorImpl(
            get()
        )
    } bind (AchievementsInteractor::class)

    single {
        AchievementsRepositoryImpl(
            get()
        )
    } bind (AchievementsRepository::class)

    single{
        AchievementsDataStoreImpl(
            getService(AchievementsService::class.java),
            GlobalContext.get().get(),
        )
    } bind (AchievementsDataStore::class)

    single {
        AchievementsDataStoreFactoryImpl(
            get()
        )
    } bind (AchievementsDataStoreFactory::class)

}

