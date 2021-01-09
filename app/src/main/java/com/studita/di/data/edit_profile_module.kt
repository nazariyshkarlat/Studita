package com.studita.di.data

import android.content.Context
import com.studita.data.net.EditProfileService
import com.studita.data.repository.EditProfileRepositoryImpl
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStore
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactory
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactoryImpl
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.edit_profile.EditProfileInteractor
import com.studita.domain.interactor.edit_profile.EditProfileInteractorImpl
import com.studita.domain.repository.EditProfileRepository
import com.studita.domain.repository.UserDataRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createEditProfileModule(config: DI.Config) = configModule(configuration = config) {

    single {
        EditProfileInteractorImpl(
            get(),
            GlobalContext.get().get(),
        )
    } bind (EditProfileInteractor::class)

    single{
        EditProfileRepositoryImpl(
            get(),
            GlobalContext.get().get()
        )
    } bind (EditProfileRepository::class)

    single {
        EditProfileDataStoreFactoryImpl(
            get()
        )
    } bind (EditProfileDataStoreFactory::class)

    single {
        EditProfileDataStoreImpl(
            GlobalContext.get().get(),
            getService(EditProfileService::class.java)
        )
    } bind (EditProfileDataStore::class)

}
