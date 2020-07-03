package com.example.studita.domain.interactor.edit_profile

import android.graphics.Bitmap
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.interactor.UserNameAvailableStatus

interface EditProfileInteractor {

    suspend fun editProfile(editProfileRequestData: EditProfileRequestData, userDataData: UserDataData, newAvatar: Bitmap?, retryCount: Int = 30): EditProfileStatus

    suspend fun isUserNameAvailable(userName: String, retryCount: Int = 30): UserNameAvailableStatus

    fun isProfileDataChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean

    fun isUserNameChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData): Boolean

    fun isValidUserNameLength(newProfileData: EditProfileData): Boolean

    fun isValidUserFullNameLength(newProfileData: EditProfileData): Boolean

    fun isValidData(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean
}