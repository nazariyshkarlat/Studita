package com.example.studita.data.repository.datasource.edit_profile

import com.example.studita.data.entity.EditProfileRequest

interface EditProfileDataStore {

    suspend fun tryEditProfile(editProfileRequest: EditProfileRequest): Int

}