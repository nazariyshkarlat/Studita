package com.example.studita.data.repository.datasource.edit_profile

import com.example.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreImpl

class EditProfileDataStoreFactoryImpl(
        private val editProfileDataStoreImpl: EditProfileDataStoreImpl
) : EditProfileDataStoreFactory {

    override fun create() =
            editProfileDataStoreImpl
}

interface EditProfileDataStoreFactory{

    fun create(): EditProfileDataStoreImpl

}