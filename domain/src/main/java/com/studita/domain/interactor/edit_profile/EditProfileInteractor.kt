package com.studita.domain.interactor.edit_profile

import android.graphics.Bitmap
import com.studita.domain.entity.EditProfileData
import com.studita.domain.entity.EditProfileRequestData
import com.studita.domain.entity.UserDataData
import com.studita.domain.interactor.EditProfileStatus
import com.studita.domain.interactor.UserNameAvailableStatus

interface EditProfileInteractor {

    companion object{
        const val NAME_MIN_LENGTH = 0
        const val NAME_MAX_LENGTH = 30
        const val USER_NAME_MIN_LENGTH = 0
        const val USER_NAME_MAX_LENGTH = 25
    }

    suspend fun editProfile(
        editProfileRequestData: EditProfileRequestData,
        userDataData: UserDataData,
        newAvatar: Bitmap?,
        retryCount: Int = 3
    ): EditProfileStatus

    suspend fun isUserNameAvailable(userName: String, retryCount: Int = 3): UserNameAvailableStatus

    fun isProfileDataChanged(
        oldProfileData: EditProfileData,
        newProfileData: EditProfileData,
        avaChanged: Boolean
    ): Boolean

    fun isUserNameChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData): Boolean

    fun isValidUserNameLength(newProfileData: EditProfileData): Boolean

    fun isValidData(
        oldProfileData: EditProfileData,
        newProfileData: EditProfileData,
        avaChanged: Boolean
    ): Boolean
}