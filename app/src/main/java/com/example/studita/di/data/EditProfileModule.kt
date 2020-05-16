package com.example.studita.di.data

import com.example.studita.data.entity.mapper.EditProfileRequestMapper
import com.example.studita.data.entity.mapper.EditProfileEntityMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.EditProfileService
import com.example.studita.data.repository.EditProfileRepositoryImpl
import com.example.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.edit_profile.EditProfileDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.edit_profile.EditProfileInteractor
import com.example.studita.domain.interactor.edit_profile.EditProfileInteractorImpl
import com.example.studita.domain.repository.EditProfileRepository
import com.example.studita.domain.repository.UserDataRepository

object EditProfileModule {

    private lateinit var config: DI.Config

    private var repository: EditProfileRepository? = null
    private var editProfileInteractor: EditProfileInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
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
                    getEditProfileRequestMapper()
            )
        return repository!!
    }

    private fun makeEditProfileIntercator(editProfileRepository: EditProfileRepository, userDataRepository: UserDataRepository) =
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

    private fun getEditProfileRequestMapper() =
            EditProfileRequestMapper(UserIdTokenMapper(), EditProfileEntityMapper())

}
