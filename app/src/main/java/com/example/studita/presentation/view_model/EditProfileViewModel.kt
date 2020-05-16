package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.EditProfileModule
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel(){

    private val editProfileInteractor = EditProfileModule.getEditProfileInteractorImpl()
    private var job: Job? = null

    var oldProfileData = EditProfileData(UserUtils.userData.userName, UserUtils.userData.userFullName, UserUtils.userData.avatarLink)
    var newProfileData = oldProfileData.copy()

    fun saveProfileChanges(){
        applyLocalChanges()
        job = viewModelScope.launchExt(job) {
            editProfileInteractor.editProfile(EditProfileRequestData(UserUtils.getUserIDTokenData()!!, newProfileData), UserUtils.userData)
        }
        oldProfileData = newProfileData.copy()
    }

    fun isProfileDataChanged(): Boolean = oldProfileData != newProfileData

    private fun applyLocalChanges(){
        UserUtils.userData.userName = newProfileData.userName
        UserUtils.userData.userFullName = newProfileData.userFullName
        UserUtils.userData.avatarLink = newProfileData.avatarLink
        UserUtils.userDataLiveData.value = UserUtils.userData
    }

}