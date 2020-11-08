package com.studita.di.data

import android.content.Context
import com.studita.data.net.EditProfileService
import com.studita.data.repository.EditProfileRepositoryImpl
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactoryImpl
import com.studita.data.repository.datasource.edit_profile.EditProfileDataStoreImpl
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.edit_profile.EditProfileInteractor
import com.studita.domain.interactor.edit_profile.EditProfileInteractorImpl
import com.studita.domain.repository.EditProfileRepository
import com.studita.domain.repository.UserDataRepository

object EditProfileModule {

    private lateinit var config: DI.Config

    private var repository: EditProfileRepository? = null
    private var editProfileInteractor: EditProfileInteractor? = null
    lateinit var applicationContext: Context

    fun initialize(configuration: DI.Config = DI.Config.RELEASE, context: Context) {
        config = configuration
        applicationContext = context
    }

    fun getEditProfileInteractorImpl(): EditProfileInteractor {
        if (config == DI.Config.RELEASE && editProfileInteractor == null)
            editProfileInteractor =
                makeEditProfileIntercator(
                    getEditProfileRepository(),
                    UserDataModule.getUserDataRepository()
                )
        return editProfileInteractor!!
    }

    private fun getEditProfileRepository(): EditProfileRepository {
        if (repository == null)
            repository = EditProfileRepositoryImpl(
                getEditProfileDataStoreFactory(),
                applicationContext
            )
        return repository!!
    }

    private fun makeEditProfileIntercator(
        editProfileRepository: EditProfileRepository,
        userDataRepository: UserDataRepository
    ) =
        EditProfileInteractorImpl(
            editProfileRepository,
            userDataRepository
        )


    private fun getEditProfileDataStoreFactory() =
        EditProfileDataStoreFactoryImpl(
            getEditProfileDataStore()
        )

    private fun getEditProfileDataStore() =
        EditProfileDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(EditProfileService::class.java)
        )

}
