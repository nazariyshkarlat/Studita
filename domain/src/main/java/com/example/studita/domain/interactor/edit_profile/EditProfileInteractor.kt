package com.example.studita.domain.interactor.edit_profile

import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.EditProfileStatus

interface EditProfileInteractor {

    suspend fun editProfile(editProfileRequestData: EditProfileRequestData, userDataData: UserDataData): EditProfileStatus

}