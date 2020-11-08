package com.studita.data.repository.datasource.edit_profile

class EditProfileDataStoreFactoryImpl(
    private val editProfileDataStoreImpl: EditProfileDataStoreImpl
) : EditProfileDataStoreFactory {

    override fun create() =
        editProfileDataStoreImpl
}

interface EditProfileDataStoreFactory {

    fun create(): EditProfileDataStoreImpl

}