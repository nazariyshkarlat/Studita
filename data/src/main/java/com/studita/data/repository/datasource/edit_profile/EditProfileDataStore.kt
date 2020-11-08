package com.studita.data.repository.datasource.edit_profile

import com.studita.data.entity.EditProfileRequest
import java.io.File

interface EditProfileDataStore {

    suspend fun tryEditProfile(
        editProfileRequest: EditProfileRequest,
        newAvatarFile: File?
    ): Pair<String?, Int>

    suspend fun isUserNameAvailable(userName: String): Pair<Int, Boolean?>
}