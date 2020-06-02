package com.example.studita.domain.interactor.edit_profile

import android.graphics.Bitmap
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.interactor.UserNameAvailableStatus
import com.example.studita.domain.repository.EditProfileRepository
import com.example.studita.domain.repository.UserDataRepository

class EditProfileInteractorImpl(private val editProfileRepository: EditProfileRepository, private val userDataRepository: UserDataRepository) : EditProfileInteractor{
    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData, userDataData: UserDataData, newAvatar: Bitmap?): EditProfileStatus =
            try {
                userDataRepository.saveUserData(userDataData)
                val pair = editProfileRepository.editProfile(editProfileRequestData, newAvatar)
                when (pair.second) {
                    200 -> EditProfileStatus.Success(pair.first)
                    else -> EditProfileStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException)
                    EditProfileStatus.NoConnection
                else
                    EditProfileStatus.ServiceUnavailable
            }

    override suspend fun isUserNameAvailable(userName: String): UserNameAvailableStatus =
            try {
                val pair = editProfileRepository.isUserNameAvailable(userName)
                val code = pair.first
                val isMyFriend = pair.second == true
                when (code) {
                    200 -> if(isMyFriend) UserNameAvailableStatus.Available else UserNameAvailableStatus.Unavailable
                    else -> UserNameAvailableStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                UserNameAvailableStatus.Failure
            }


    override fun isProfileDataChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean = avaChanged or (oldProfileData != newProfileData)
    override fun isUserNameChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData): Boolean = oldProfileData.userName != newProfileData.userName

    override fun isValidUserNameLength(newProfileData: EditProfileData): Boolean = newProfileData.userName?.length in 4..25

    override fun isValidUserFullNameLength(newProfileData: EditProfileData): Boolean = newProfileData.userFullName?.length  in 2..30 || newProfileData.userFullName == null

    override fun isValidData(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean = isProfileDataChanged(oldProfileData, newProfileData, avaChanged)
            && isValidUserNameLength(newProfileData)
            && isValidUserFullNameLength(newProfileData)

}