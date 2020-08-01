package com.example.studita.domain.interactor.edit_profile

import android.graphics.Bitmap
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.interactor.UserNameAvailableStatus
import com.example.studita.domain.repository.EditProfileRepository
import com.example.studita.domain.repository.UserDataRepository
import kotlinx.coroutines.delay

class EditProfileInteractorImpl(private val editProfileRepository: EditProfileRepository, private val userDataRepository: UserDataRepository) : EditProfileInteractor{

    private val retryDelay = 1000L

    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData, userDataData: UserDataData, newAvatar: Bitmap?, retryCount: Int): EditProfileStatus =
            try {
                userDataRepository.saveUserData(userDataData)
                val pair = editProfileRepository.editProfile(editProfileRequestData, newAvatar)
                when (pair.second) {
                    200 -> EditProfileStatus.Success(pair.first)
                    else -> EditProfileStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if(e is NetworkConnectionException || e is ServerUnavailableException) {
                    if (retryCount == 0) {
                        if (e is NetworkConnectionException)
                            EditProfileStatus.NoConnection
                        else
                            EditProfileStatus.ServiceUnavailable
                    } else {
                        if (e is NetworkConnectionException)
                            delay(retryDelay)
                        editProfile(editProfileRequestData, userDataData, newAvatar, retryCount - 1)
                    }
                }else
                    EditProfileStatus.Failure
            }

    override suspend fun isUserNameAvailable(userName: String, retryCount: Int): UserNameAvailableStatus =
            try {
                val pair = editProfileRepository.isUserNameAvailable(userName)
                val code = pair.first
                val isMyFriend = pair.second == true
                when (code) {
                    200 -> if(isMyFriend) UserNameAvailableStatus.Available else UserNameAvailableStatus.IsTaken
                    else -> UserNameAvailableStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if(e is NetworkConnectionException || e is ServerUnavailableException) {
                    if (retryCount == 0) {
                        UserNameAvailableStatus.Failure
                    } else {
                        if (e is NetworkConnectionException)
                            delay(retryDelay)
                        isUserNameAvailable(userName, retryCount-1)
                    }
                }else
                    UserNameAvailableStatus.Failure
            }


    override fun isProfileDataChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean = avaChanged or (oldProfileData != newProfileData)
    override fun isUserNameChanged(oldProfileData: EditProfileData, newProfileData: EditProfileData): Boolean = oldProfileData.userName != newProfileData.userName

    override fun isValidUserNameLength(newProfileData: EditProfileData): Boolean = newProfileData.userName?.length in 4..25

    override fun isValidData(oldProfileData: EditProfileData, newProfileData: EditProfileData, avaChanged: Boolean): Boolean = isProfileDataChanged(oldProfileData, newProfileData, avaChanged)
            && isValidUserNameLength(newProfileData)
}