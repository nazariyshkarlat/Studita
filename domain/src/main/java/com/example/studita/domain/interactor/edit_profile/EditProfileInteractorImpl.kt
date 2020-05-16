package com.example.studita.domain.interactor.edit_profile

import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.repository.EditProfileRepository
import com.example.studita.domain.repository.UserDataRepository

class EditProfileInteractorImpl(private val editProfileRepository: EditProfileRepository, private val userDataRepository: UserDataRepository) : EditProfileInteractor{
    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData, userDataData: UserDataData): EditProfileStatus =
            try {
                userDataRepository.saveUserData(userDataData)
                when (editProfileRepository.editProfile(editProfileRequestData)) {
                    200 -> EditProfileStatus.Success
                    else -> EditProfileStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException)
                    EditProfileStatus.NoConnection
                else
                    EditProfileStatus.ServiceUnavailable
            }

}